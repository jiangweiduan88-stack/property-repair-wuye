<template>
  <div class="register">
    <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="register-form">
      <h3 class="title">{{ title }}</h3>
      <div class="section-title">账号信息</div>
      <el-form-item prop="username">
        <el-input 
          v-model="registerForm.username" 
          type="text" 
          size="large" 
          auto-complete="off" 
          placeholder="账号"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          size="large" 
          auto-complete="off"
          placeholder="密码"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          size="large" 
          auto-complete="off"
          placeholder="确认密码"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <div class="section-title">房屋绑定信息</div>
      <el-form-item prop="ownerName">
        <el-input v-model="registerForm.ownerName" size="large" placeholder="业主姓名" />
      </el-form-item>
      <el-form-item prop="phonenumber">
        <el-input v-model="registerForm.phonenumber" size="large" maxlength="11" placeholder="手机号码" />
      </el-form-item>
      <el-form-item prop="buildingId">
        <el-select v-model="registerForm.buildingId" size="large" placeholder="请选择楼栋" style="width: 100%">
          <el-option
            v-for="building in buildingOptions"
            :key="building.buildingId"
            :label="building.buildingName"
            :value="building.buildingId"
          />
        </el-select>
      </el-form-item>
      <div class="room-row">
        <el-form-item prop="unitNo">
          <el-input v-model="registerForm.unitNo" size="large" maxlength="30" placeholder="单元（可选）" />
        </el-form-item>
        <el-form-item prop="roomNo">
          <el-input v-model="registerForm.roomNo" size="large" maxlength="30" placeholder="房号" />
        </el-form-item>
      </div>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          size="large" 
          v-model="registerForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter="handleRegister"
        >
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <div class="register-code">
          <img :src="codeUrl" @click="getCode" class="register-code-img"/>
        </div>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="large" 
          type="primary"
          style="width:100%;"
          @click.prevent="handleRegister"
        >
          <span v-if="!loading">注 册</span>
          <span v-else>注 册 中...</span>
        </el-button>
        <div style="float: right;">
          <router-link class="link-type" :to="'/login'">使用已有账户登录</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-register-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from "element-plus"
import { getCodeImg, getRegisterBuildings, register } from "@/api/login"
import defaultSettings from '@/settings'
import { buildPasswordRules } from '@/utils/passwordRule'

const title = import.meta.env.VITE_APP_TITLE
const footerContent = defaultSettings.footerContent
const router = useRouter()
const { proxy } = getCurrentInstance()

const registerForm = ref({
  username: "",
  password: "",
  confirmPassword: "",
  ownerName: "",
  phonenumber: "",
  buildingId: undefined,
  unitNo: "",
  roomNo: "",
  code: "",
  uuid: ""
})

const equalToPassword = (rule, value, callback) => {
  if (registerForm.value.password !== value) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, trigger: "blur", message: "请输入您的账号" },
    { min: 2, max: 20, message: "用户账号长度必须介于 2 和 20 之间", trigger: "blur" }
  ],
  password: buildPasswordRules({
    type: '0',
    requiredMessage: '请输入您的密码',
    lengthMessage: '用户密码长度必须介于 6 和 20 之间'
  }),
  confirmPassword: [
    { required: true, trigger: "blur", message: "请再次输入您的密码" },
    { required: true, validator: equalToPassword, trigger: "blur" }
  ],
  ownerName: [
    { required: true, trigger: "blur", message: "请输入业主姓名" },
    { min: 2, max: 20, message: "业主姓名长度必须介于 2 和 20 之间", trigger: "blur" }
  ],
  phonenumber: [
    { required: true, trigger: "blur", message: "请输入手机号码" },
    { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的11位手机号码", trigger: "blur" }
  ],
  buildingId: [{ required: true, trigger: "change", message: "请选择楼栋" }],
  roomNo: [{ required: true, trigger: "blur", message: "请输入房号" }],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
}

const codeUrl = ref("")
const loading = ref(false)
const captchaEnabled = ref(true)
const buildingOptions = ref([])

// 页面先做即时格式校验，后端仍会重复校验并负责最终的数据安全约束。
function handleRegister() {
  proxy.$refs.registerRef.validate(valid => {
    if (valid) {
      loading.value = true
      register(registerForm.value).then(res => {
        const username = registerForm.value.username
        loading.value = false
        // 用户确认成功提示后返回登录页，避免停留在可重复提交的注册表单。
        ElMessageBox.alert("恭喜你，您的账号 " + username + " 注册成功！", "系统提示", {
          type: "success",
          confirmButtonText: "返回登录",
          showClose: false,
          closeOnClickModal: false,
          closeOnPressEscape: false
        }).finally(() => {
          router.replace("/login")
        })
      }).catch(() => {
        loading.value = false
        if (captchaEnabled.value) {
          getCode()
        }
      })
    }
  })
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img
      registerForm.value.uuid = res.uuid
    }
  })
}

function loadBuildings() {
  // 注册页只加载状态正常的楼栋，减少无效房屋关系进入系统的可能。
  getRegisterBuildings().then(res => {
    buildingOptions.value = res.data || []
  })
}

getCode()
loadBuildings()
</script>

<style lang='scss' scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100%;
  padding: 24px 16px 64px;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 520px;
  max-width: calc(100vw - 32px);
  max-height: calc(100vh - 96px);
  overflow-y: auto;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
}
.section-title {
  margin: 2px 0 12px;
  color: #49655d;
  font-size: 13px;
  font-weight: 700;
}
.room-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
@media screen and (max-width: 560px) {
  .room-row {
    grid-template-columns: 1fr;
    gap: 0;
  }
}
.register-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.register-code {
  width: 33%;
  height: 40px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.register-code-img {
  height: 40px;
  padding-left: 12px;
}
</style>
