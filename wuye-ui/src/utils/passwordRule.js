/** 读取系统密码策略并生成校验规则，保证注册、重置和修改密码使用同一标准。 */
import cache from '@/plugins/cache'

const PASSWORD_RULES = {
  '0': { pattern: /^[^<>"'|\\]+$/, message: '密码不能包含非法字符：< > " \' \\ |' },
  '1': { pattern: /^[0-9]+$/, message: '密码只能为数字（0-9）' },
  '2': { pattern: /^[a-zA-Z]+$/, message: '密码只能为英文字母（a-z、A-Z）' },
  '3': { pattern: /^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$/, message: '密码必须同时包含字母和数字' },
  '4': { pattern: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()\-=_+])[A-Za-z\d~!@#$%^&*()\-=_+]+$/, message: '密码必须同时包含字母、数字和特殊字符（~!@#$%^&*()-=_+）' }
}

function resolveType(type) {
  return String(type ?? cache.session.get('pwrChrtype') ?? '0')
}

export function buildPasswordRules(options = {}) {
  const {
    type,
    min = 6,
    max = 20,
    requiredMessage = '密码不能为空',
    lengthMessage = `密码长度必须介于 ${min} 和 ${max} 之间`
  } = options
  const rule = PASSWORD_RULES[resolveType(type)] || PASSWORD_RULES['0']
  return [
    { required: true, message: requiredMessage, trigger: 'blur' },
    { min, max, message: lengthMessage, trigger: 'blur' },
    { pattern: rule.pattern, message: rule.message, trigger: 'blur' }
  ]
}

export function validatePassword(value, options = {}) {
  const {
    type,
    min = 6,
    max = 20,
    lengthMessage = `密码长度必须介于 ${min} 和 ${max} 之间`
  } = options
  if (!value || value.length < min || value.length > max) {
    return lengthMessage
  }
  const rule = PASSWORD_RULES[resolveType(type)] || PASSWORD_RULES['0']
  return rule.pattern.test(value) ? true : rule.message
}
