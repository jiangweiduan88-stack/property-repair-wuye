package com.wuye.system.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wuye.common.exception.ServiceException;
import com.wuye.common.core.domain.entity.SysRole;
import com.wuye.common.core.domain.entity.SysUser;
import com.wuye.common.utils.SecurityUtils;
import com.wuye.common.utils.uuid.IdUtils;
import com.wuye.system.domain.PropRepairEvaluation;
import com.wuye.system.domain.PropRepairOrder;
import com.wuye.system.domain.PropRepairStats;
import com.wuye.system.domain.PropRepairCategory;
import com.wuye.system.domain.PropRoom;
import com.wuye.system.mapper.PropRepairEvaluationMapper;
import com.wuye.system.mapper.PropRepairOrderMapper;
import com.wuye.system.service.IPropRepairOrderLogService;
import com.wuye.system.service.IPropRepairOrderService;
import com.wuye.system.service.IPropRepairCategoryService;
import com.wuye.system.service.IPropRoomService;
import com.wuye.system.service.ISysUserService;

/**
 * 报修工单核心业务服务。
 *
 * 统一维护工单状态机、角色权限、房屋和分类有效性、评价生成及操作日志。
 * 关键写操作使用事务和“按预期状态更新”，可防止重复点击或并发请求造成越级流转。
 */
@Service
public class PropRepairOrderServiceImpl extends ServiceImpl<PropRepairOrderMapper, PropRepairOrder> implements IPropRepairOrderService
{
    @Autowired
    private PropRepairOrderMapper orderMapper;

    @Autowired
    private PropRepairEvaluationMapper evaluationMapper;

    @Autowired
    private IPropRepairOrderLogService logService;

    @Autowired
    private IPropRoomService roomService;

    @Autowired
    private IPropRepairCategoryService categoryService;

    @Autowired
    private ISysUserService userService;

    // 按角色数据范围和查询条件读取工单列表。
    public List<PropRepairOrder> selectOrderList(PropRepairOrder order) { return orderMapper.selectOrderList(order); }
    // 根据主键读取完整工单，供详情、操作和权限校验复用。
    public PropRepairOrder selectOrderById(Long orderId) { return orderMapper.selectOrderById(orderId); }

    // 校验房屋和分类后生成业务工单号，并记录首次提交动作。
    @Transactional
    public int insertOrder(PropRepairOrder order)
    {
        validateNewOrder(order);
        // 时间戳加随机片段兼顾可读性和低碰撞概率，便于线下沟通时快速定位工单。
        order.setOrderNo("BX" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) + IdUtils.fastSimpleUUID().substring(0, 6).toUpperCase());
        order.setStatus("0");
        int rows = orderMapper.insertOrder(order);
        recordLog(order, "submit", "\u5df2\u63d0\u4ea4", "");
        return rows;
    }

    // 仅更新允许编辑的白名单字段，驳回工单修改后自动重新提交。
    @Transactional
    public int updateOrder(PropRepairOrder order)
    {
        // 只允许待受理或已驳回工单修改；驳回后编辑会重新进入待受理流程。
        PropRepairOrder oldOrder = requireStatus(order.getOrderId(), "0", "6");
        ensureOwnerOrManager(oldOrder, "只有工单所属业主可以修改此工单");
        PropRepairOrder updateOrder = buildEditableOrder(oldOrder, order);
        validateEditableOrder(updateOrder, oldOrder.getOwnerId());
        String changeSummary = describeOrderChanges(oldOrder, updateOrder);
        if ("6".equals(oldOrder.getStatus()))
        {
            updateOrder.setStatus("0");
            updateOrder.setRejectReason("");
        }
        else
        {
            updateOrder.setStatus(oldOrder.getStatus());
        }
        int rows = updateOrderIfCurrent(updateOrder, oldOrder.getStatus());
        if (rows > 0)
        {
            // 日志只记录实际完成的更新，并列出变化字段，方便追溯而不保存重复全文。
            PropRepairOrder latestOrder = orderMapper.selectOrderById(order.getOrderId());
            if ("6".equals(oldOrder.getStatus()))
            {
                String content = changeSummary.isEmpty()
                        ? "\u9a73\u56de\u540e\u91cd\u65b0\u63d0\u4ea4\u5de5\u5355"
                        : "\u9a73\u56de\u540e\u91cd\u65b0\u63d0\u4ea4\u5de5\u5355\uff0c\u4fee\u6539\u9879\uff1a" + changeSummary;
                recordLog(latestOrder, "resubmit", "\u91cd\u65b0\u63d0\u4ea4", content);
            }
            else
            {
                String content = changeSummary.isEmpty()
                        ? "\u4fee\u6539\u4e86\u5de5\u5355\u4fe1\u606f"
                        : "\u4fee\u6539\u9879\uff1a" + changeSummary;
                recordLog(latestOrder, "update", "\u5df2\u4fee\u6539", content);
            }
        }
        return rows;
    }

    // 删除工单属于管理操作，普通业主和维修人员不得执行。
    public int deleteOrderByIds(Long[] orderIds)
    {
        if (!isOrderManager())
        {
            throw new ServiceException("只有系统管理员或物业管理员可以删除报修工单");
        }
        return orderMapper.deleteOrderByIds(orderIds);
    }

    // 将待受理工单推进到已受理，并记录对应操作日志。
    @Transactional
    public int accept(Long orderId)
    {
        // 状态 0 -> 1：物业受理业主提交的工单。
        PropRepairOrder order = requireStatus(orderId, "0");
        order.setStatus("1");
        int rows = updateOrderIfCurrent(order, "0");
        recordLog(order, "accept", "\u5df2\u53d7\u7406", "");
        return rows;
    }

    // 驳回待受理工单并保存原因，供业主有针对性地修改。
    @Transactional
    public int reject(Long orderId, String reason)
    {
        // 状态 0 -> 6：驳回原因必填，便于业主有针对性地修改后重新提交。
        reason = requireText(reason, "请填写驳回原因", 500);
        PropRepairOrder order = requireStatus(orderId, "0");
        order.setStatus("6");
        order.setRejectReason(reason);
        int rows = updateOrderIfCurrent(order, "0");
        recordLog(order, "reject", "\u5df2\u9a73\u56de", "\u9a73\u56de\u539f\u56e0\uff1a" + (reason != null ? reason : ""));
        return rows;
    }

    // 将已受理或返工工单分配给经过角色校验的维修人员。
    @Transactional
    public int assign(Long orderId, Long repairUserId, String repairUserName)
    {
        // 状态 1/8 -> 2：首次分配和返工重新分配共用入口，并验证目标账号确有维修角色。
        SysUser repairUser = requireRepairUser(repairUserId);
        PropRepairOrder order = requireStatus(orderId, "1", "8");
        String expectedStatus = order.getStatus();
        order.setStatus("2");
        order.setRepairUserId(repairUser.getUserId());
        order.setRepairUserName(repairUser.getNickName());
        int rows = updateOrderIfCurrent(order, expectedStatus);
        recordLog(order, "assign", "\u5df2\u5206\u914d", "\u5206\u914d\u7ed9\uff1a" + repairUser.getNickName());
        return rows;
    }

    // 由被分配维修人员开始处理工单，阻止其他账号越权接单。
    @Transactional
    public int start(Long orderId)
    {
        // 状态 2 -> 3：只有被分配人员或管理员能够开始维修。
        PropRepairOrder order = requireStatus(orderId, "2");
        ensureAssignedRepairUser(order, "只有已分配的维修人员可以开始维修此工单");
        order.setStatus("3");
        int rows = updateOrderIfCurrent(order, "2");
        recordLog(order, "start", "\u7ef4\u4fee\u4e2d", "");
        return rows;
    }

    // 保存维修结果和图片后进入待确认状态，不直接结束工单。
    @Transactional
    public int finish(Long orderId, String finishResult, String finishImages)
    {
        // 状态 3 -> 4：维修人员提交结果后交由业主确认，不直接结束工单。
        finishResult = requireText(finishResult, "请填写维修结果", 1000);
        if (finishImages != null && finishImages.length() > 1000)
        {
            throw new ServiceException("维修图片信息长度不能超过1000个字符");
        }
        PropRepairOrder order = requireStatus(orderId, "3");
        ensureAssignedRepairUser(order, "只有已分配的维修人员可以完成此工单");
        order.setStatus("4");
        order.setFinishResult(finishResult);
        order.setFinishImages(finishImages);
        int rows = updateOrderIfCurrent(order, "3");
        recordLog(order, "finish", "\u5f85\u786e\u8ba4", "\u7ef4\u4fee\u7ed3\u679c\uff1a" + (finishResult != null ? finishResult : ""));
        return rows;
    }

    // 业主确认结果并同步生成评价，事务保证两项数据同时成功。
    @Transactional
    public int confirm(Long orderId, PropRepairEvaluation evaluation)
    {
        // 状态 4 -> 5：确认与评价在同一事务中完成，避免工单完成但评价写入失败。
        PropRepairOrder order = requireStatus(orderId, "4");
        ensureOwner(order, "只有工单所属业主可以确认维修结果");
        if (evaluation == null || evaluation.getScore() == null || evaluation.getScore() < 1 || evaluation.getScore() > 5)
        {
            throw new ServiceException("请选择服务评分");
        }
        if (evaluation.getContent() != null && evaluation.getContent().trim().length() > 500)
        {
            throw new ServiceException("评价内容长度不能超过500个字符");
        }
        if (evaluationMapper.selectEvaluationByOrderId(orderId) != null)
        {
            throw new ServiceException("此报修工单已经完成评价，请勿重复评价");
        }
        order.setStatus("5");
        int rows = updateOrderIfCurrent(order, "4");
        evaluation.setOrderId(order.getOrderId());
        evaluation.setOrderNo(order.getOrderNo());
        evaluation.setOwnerId(order.getOwnerId());
        evaluation.setRepairUserId(order.getRepairUserId());
        evaluationMapper.insertEvaluation(evaluation);
        recordLog(order, "confirm", "\u5df2\u5b8c\u6210", "\u8bc4\u5206\uff1a" + evaluation.getScore() + (evaluation.getContent() != null && !evaluation.getContent().isEmpty() ? "\uff0c\u8bc4\u4ef7\uff1a" + evaluation.getContent() : ""));
        return rows;
    }

    // 允许业主或管理员取消尚未受理的工单，并保留操作记录。
    @Transactional
    public int cancel(Long orderId)
    {
        PropRepairOrder order = requireStatus(orderId, "0");
        ensureOwnerOrManager(order, "只有工单所属业主可以取消此工单");
        order.setStatus("7");
        int rows = updateOrderIfCurrent(order, "0");
        recordLog(order, "cancel", "\u5df2\u53d6\u6d88", "");
        return rows;
    }

    // 对待确认工单发起返工，将原因带入下一轮维修流程。
    @Transactional
    public int rework(Long orderId, String reason)
    {
        // 状态 4 -> 8：业主不认可维修结果时发起返工，随后由物业重新分配。
        reason = requireText(reason, "请填写返工原因", 300);
        PropRepairOrder order = requireStatus(orderId, "4");
        ensureOwnerOrManager(order, "只有工单所属业主或物业管理员可以发起返工");
        order.setStatus("8");
        order.setRejectReason(reason);
        int rows = updateOrderIfCurrent(order, "4");
        recordLog(order, "rework", "\u8fd4\u5de5\u4e2d", "\u8fd4\u5de5\u539f\u56e0\uff1a" + (reason != null ? reason : ""));
        return rows;
    }

    // 查询不限定周期的总体统计，兼容旧统计接口调用。
    public PropRepairStats selectRepairStats()
    {
        return orderMapper.selectRepairStats(
            LocalDateTime.of(1000, 1, 1, 0, 0),
            LocalDateTime.of(9999, 12, 31, 23, 59, 59));
    }

    // 根据月份或滚动天数统一计算看板汇总、状态、分类和人员工作量。
    public Map<String, Object> selectDashboard(String period)
    {
        // 后端统一换算统计起止时间，保证多个图表始终使用相同统计口径。
        String normalizedPeriod = period == null ? "month" : period.trim().toLowerCase();
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        switch (normalizedPeriod)
        {
            case "month":
                startDate = today.withDayOfMonth(1);
                break;
            case "30d":
                startDate = today.minusDays(29);
                break;
            case "90d":
                startDate = today.minusDays(89);
                break;
            case "180d":
                startDate = today.minusDays(179);
                break;
            default:
                throw new ServiceException("不支持的统计周期");
        }
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = today.plusDays(1).atStartOfDay();

        Map<String, Object> data = new HashMap<>();
        data.put("period", normalizedPeriod);
        data.put("startDate", startDate.toString());
        data.put("endDate", today.toString());
        data.put("summary", orderMapper.selectRepairStats(startTime, endTime));
        data.put("statusStats", orderMapper.selectStatusStats(startTime, endTime));
        data.put("categoryStats", orderMapper.selectCategoryStats(startTime, endTime));
        data.put("repairUserStats", orderMapper.selectRepairUserStats(startTime, endTime));
        return data;
    }

    private PropRepairOrder requireStatus(Long orderId, String... statuses)
    {
        // 所有状态操作先走状态机校验，阻止跳步、重复确认等非法流程。
        PropRepairOrder order = orderMapper.selectOrderById(orderId);
        if (order == null)
        {
            throw new ServiceException("报修工单不存在");
        }
        for (String status : statuses)
        {
            if (status.equals(order.getStatus()))
            {
                return order;
            }
        }
        throw new ServiceException("当前工单状态不允许执行此操作");
    }

    private int updateOrderIfCurrent(PropRepairOrder order, String expectedStatus)
    {
        // SQL 同时匹配主键和旧状态，相当于轻量乐观锁，可识别并发状态变化。
        int rows = orderMapper.updateOrderIfStatus(order, expectedStatus);
        if (rows == 0)
        {
            throw new ServiceException("工单状态已发生变化，请刷新列表后重试");
        }
        return rows;
    }

    private void ensureOwnerOrManager(PropRepairOrder order, String message)
    {
        // 管理角色可代办，普通账号则继续执行业主归属检查。
        if (isOrderManager())
        {
            return;
        }
        ensureOwner(order, message);
    }

    private void ensureOwner(PropRepairOrder order, String message)
    {
        // 使用登录用户编号与工单业主编号比较，避免信任客户端身份参数。
        Long userId = SecurityUtils.getUserId();
        if (order.getOwnerId() == null || !userId.equals(order.getOwnerId()))
        {
            throw new ServiceException(message);
        }
    }

    private void ensureAssignedRepairUser(PropRepairOrder order, String message)
    {
        // 非管理账号必须是当前分配人员才能执行维修状态操作。
        if (isOrderManager())
        {
            return;
        }
        Long userId = SecurityUtils.getUserId();
        if (order.getRepairUserId() == null || !userId.equals(order.getRepairUserId()))
        {
            throw new ServiceException(message);
        }
    }

    private PropRepairOrder buildEditableOrder(PropRepairOrder oldOrder, PropRepairOrder requestOrder)
    {
        // 使用白名单构造更新对象，避免前端通过完整实体修改业主、工单号等受保护字段。
        PropRepairOrder updateOrder = new PropRepairOrder();
        updateOrder.setOrderId(oldOrder.getOrderId());
        updateOrder.setRoomId(requestOrder.getRoomId() != null ? requestOrder.getRoomId() : oldOrder.getRoomId());
        updateOrder.setCategoryId(requestOrder.getCategoryId() != null ? requestOrder.getCategoryId() : oldOrder.getCategoryId());
        updateOrder.setTitle(requestOrder.getTitle() != null ? requestOrder.getTitle() : oldOrder.getTitle());
        updateOrder.setContent(requestOrder.getContent() != null ? requestOrder.getContent() : oldOrder.getContent());
        updateOrder.setImages(requestOrder.getImages() != null ? requestOrder.getImages() : oldOrder.getImages());
        updateOrder.setRemark(requestOrder.getRemark() != null ? requestOrder.getRemark() : oldOrder.getRemark());
        updateOrder.setUpdateBy(requestOrder.getUpdateBy());
        return updateOrder;
    }

    private void validateNewOrder(PropRepairOrder order)
    {
        if (order == null)
        {
            throw new ServiceException("报修工单信息不能为空");
        }
        validateOrderText(order);
        PropRoom room = requireActiveRoom(order.getRoomId());
        requireActiveCategory(order.getCategoryId());
        if (isOrderManager())
        {
            // 管理员代建工单时，以房屋实际绑定业主为准，保证数据归属一致。
            if (room.getOwnerId() == null)
            {
                throw new ServiceException("所选房屋尚未关联业主，不能创建报修工单");
            }
            order.setOwnerId(room.getOwnerId());
        }
        else if (room.getOwnerId() == null || !room.getOwnerId().equals(order.getOwnerId()))
        {
            throw new ServiceException("只能为本人名下的房屋提交报修工单");
        }
    }

    private void validateEditableOrder(PropRepairOrder order, Long ownerId)
    {
        // 修改时重新验证房屋归属，防止把工单迁移到他人房屋。
        validateOrderText(order);
        PropRoom room = requireActiveRoom(order.getRoomId());
        requireActiveCategory(order.getCategoryId());
        if (room.getOwnerId() == null || !room.getOwnerId().equals(ownerId))
        {
            throw new ServiceException("工单只能关联到所属业主名下的房屋");
        }
    }

    private void validateOrderText(PropRepairOrder order)
    {
        // 集中限制标题、内容和图片地址长度，保证新增与修改规则一致。
        order.setTitle(requireText(order.getTitle(), "请填写报修标题", 120));
        order.setContent(requireText(order.getContent(), "请填写报修内容", 1000));
        if (order.getImages() != null && order.getImages().length() > 1000)
        {
            throw new ServiceException("现场图片信息长度不能超过1000个字符");
        }
    }

    private PropRoom requireActiveRoom(Long roomId)
    {
        // 只允许正常房屋创建或承载工单，停用房屋不可继续报修。
        if (roomId == null)
        {
            throw new ServiceException("请选择报修房屋");
        }
        PropRoom room = roomService.selectRoomById(roomId);
        if (room == null)
        {
            throw new ServiceException("所选房屋不存在");
        }
        if (!"0".equals(room.getStatus()))
        {
            throw new ServiceException("所选房屋已停用");
        }
        return room;
    }

    private void requireActiveCategory(Long categoryId)
    {
        // 只允许使用正常报修分类，避免新工单引用已停用配置。
        if (categoryId == null)
        {
            throw new ServiceException("请选择报修分类");
        }
        PropRepairCategory category = categoryService.selectCategoryById(categoryId);
        if (category == null)
        {
            throw new ServiceException("所选报修分类不存在");
        }
        if (!"0".equals(category.getStatus()))
        {
            throw new ServiceException("所选报修分类已停用");
        }
    }

    private SysUser requireRepairUser(Long repairUserId)
    {
        // 校验目标账号状态及维修角色，防止将任务分配给无效人员。
        if (repairUserId == null)
        {
            throw new ServiceException("请选择维修人员");
        }
        SysUser user = userService.selectUserById(repairUserId);
        if (user == null || !"0".equals(user.getStatus()) || !"0".equals(user.getDelFlag()))
        {
            throw new ServiceException("所选维修人员不存在或已停用");
        }
        boolean repairWorker = false;
        // 不能只检查用户存在，还要确认其拥有启用中的维修人员角色。
        if (user.getRoles() != null)
        {
            for (SysRole role : user.getRoles())
            {
                if ("repair_worker".equals(role.getRoleKey()) && "0".equals(role.getStatus()))
                {
                    repairWorker = true;
                    break;
                }
            }
        }
        if (!repairWorker)
        {
            throw new ServiceException("所选用户不具备维修人员角色");
        }
        return user;
    }

    private String requireText(String value, String emptyMessage, int maxLength)
    {
        // 统一处理必填文本的去空格和最大长度规则。
        String text = value == null ? "" : value.trim();
        if (text.isEmpty())
        {
            throw new ServiceException(emptyMessage);
        }
        if (text.length() > maxLength)
        {
            throw new ServiceException(emptyMessage.replace("请填写", "") + "长度不能超过" + maxLength + "个字符");
        }
        return text;
    }

    private boolean isOrderManager()
    {
        // 集中定义拥有全量工单管理权限的角色集合。
        return SecurityUtils.isAdmin()
                || SecurityUtils.hasRole("system_admin")
                || SecurityUtils.hasRole("property_manager");
    }

    private String describeOrderChanges(PropRepairOrder oldOrder, PropRepairOrder newOrder)
    {
        // 对允许编辑的字段生成中文变化摘要，提升操作记录的可读性。
        // 生成面向用户的中文变化摘要，供操作记录弹窗直接展示。
        List<String> changes = new ArrayList<>();
        if (!Objects.equals(oldOrder.getRoomId(), newOrder.getRoomId()))
        {
            changes.add("\u623f\u5c4b");
        }
        if (!Objects.equals(oldOrder.getCategoryId(), newOrder.getCategoryId()))
        {
            changes.add("\u62a5\u4fee\u5206\u7c7b");
        }
        if (!sameText(oldOrder.getTitle(), newOrder.getTitle()))
        {
            changes.add("\u6807\u9898");
        }
        if (!sameText(oldOrder.getContent(), newOrder.getContent()))
        {
            changes.add("\u62a5\u4fee\u5185\u5bb9");
        }
        if (!sameText(oldOrder.getImages(), newOrder.getImages()))
        {
            changes.add("\u73b0\u573a\u56fe\u7247");
        }
        if (!sameText(oldOrder.getRemark(), newOrder.getRemark()))
        {
            changes.add("\u5907\u6ce8");
        }
        return String.join("\u3001", changes);
    }

    private boolean sameText(String left, String right)
    {
        // 归一空值和首尾空格后比较文本，避免记录无实际变化的修改。
        return Objects.equals(normalizeText(left), normalizeText(right));
    }

    private String normalizeText(String value)
    {
        // 将空值转换为空字符串，为比较和日志拼接提供稳定输入。
        return value == null ? "" : value.trim();
    }

    private void recordLog(PropRepairOrder order, String action, String actionLabel, String content)
    {
        // 统一写入操作人昵称和动作标签，使所有状态节点具备一致审计信息。
        // 审计日志是辅助能力；日志异常不能回滚已经合法完成的主业务操作。
        try
        {
            Long operatorId = null;
            String operatorName = "";
            try
            {
                operatorId = SecurityUtils.getUserId();
                operatorName = SecurityUtils.getUsername();
            }
            catch (Exception e)
            {
                // Ignore missing security context for background or test calls.
            }
            logService.insertLog(order.getOrderId(), order.getOrderNo(), action, actionLabel, operatorId, operatorName, content);
        }
        catch (Exception e)
        {
            // Log failures must not break the main business flow.
        }
    }
}
