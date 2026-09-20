<template>
  <div class="login">
    <section class="login-visual">
      <div class="brand-mark">
        <img src="@/assets/logo/logo.png" alt="物业报修系统">
        <span>物业报修系统</span>
      </div>
      <div class="visual-copy">
        <p class="eyebrow">社区物业报修服务</p>
        <h1>让报修、派单、维修、评价都清晰可追踪</h1>
        <p class="summary">面向业主、物业管理员和维修人员的社区报修协同平台。</p>
      </div>
      <div class="process-strip">
        <span>提交报修</span>
        <span>物业派单</span>
        <span>维修处理</span>
        <span>业主评价</span>
      </div>
    </section>

    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <div class="form-heading">
        <p>欢迎回来</p>
        <h2>{{ title }}</h2>
      </div>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="请输入账号"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="请输入密码"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item v-if="captchaEnabled" prop="code">
        <el-input
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="请输入验证码"
          class="code-input"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" class="login-code-img" @click="getCode">
        </div>
      </el-form-item>
      <div class="form-options">
        <div class="password-options">
          <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
          <button type="button" class="forgot-password" @click="openForgotPassword">忘记密码</button>
        </div>
        <router-link v-if="register" class="link-type" :to="'/register'">立即注册</router-link>
      </div>
      <el-form-item class="login-action">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          @click.prevent="handleLogin"
        >
          <span v-if="!loading">登录系统</span>
          <span v-else>正在登录...</span>
        </el-button>
      </el-form-item>
    </el-form>
    <el-dialog
      v-model="forgotPasswordVisible"
      title="幸福社区物业服务中心"
      width="480px"
      class="service-center-dialog"
      append-to-body
      align-center
    >
      <div class="service-center-content" v-loading="contactLoading">
        <p class="service-center-intro">如需找回密码，请联系物业服务中心协助处理。</p>
        <p v-if="contactError" class="contact-error" role="alert">{{ contactError }}</p>
        <dl class="contact-list">
          <div><dt>部门名称</dt><dd>{{ serviceCenter.deptName || '暂未提供' }}</dd></div>
          <div><dt>负责人</dt><dd>{{ serviceCenter.leader || '暂未提供' }}</dd></div>
          <div><dt>联系电话</dt><dd>{{ serviceCenter.phone || '暂未提供' }}</dd></div>
          <div><dt>邮箱</dt><dd>{{ serviceCenter.email || '暂未提供' }}</dd></div>
          <div class="service-time">
            <dt>服务时间</dt>
            <dd>周一至周五<br>上午 8:00 - 12:00<br>下午 14:00 - 18:00</dd>
          </div>
        </dl>
      </div>
    </el-dialog>
    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg, getServiceCenterContact } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'
import useUserStore from '@/store/modules/user'

export default {
  name: "Login",
  data() {
    return {
      title: '物业报修系统',
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true,
      register: false,
      forgotPasswordVisible: false,
      contactLoading: false,
      contactError: '',
      serviceCenter: { deptName: '', leader: '', phone: '', email: '' },
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    // 忘记密码不直接提供高风险的自助重置，而是读取物业公开联系方式引导线下核验身份。
    openForgotPassword() {
      this.forgotPasswordVisible = true
      this.contactLoading = true
      this.contactError = ''
      this.serviceCenter = { deptName: '', leader: '', phone: '', email: '' }
      getServiceCenterContact().then(res => {
        this.serviceCenter = res.data || this.serviceCenter
      }).catch(() => {
        this.contactError = '暂无法获取联系信息，请稍后重试。'
      }).finally(() => {
        this.contactLoading = false
      })
    },
    getCode() {
      getCodeImg().then(res => {
        // 登录初始化接口同时下发验证码和注册开关，使页面入口与后端配置保持一致。
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        this.register = res.registerEnabled === true
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      // 仅在用户主动勾选时恢复本地凭据，密码仍按项目既有方式加密存储。
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
        code: '',
        uuid: this.loginForm.uuid || ''
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          // 先同步“记住密码”状态，再由 Pinia 用户仓库统一处理令牌和登录态。
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          useUserStore().login(this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(() => {})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 460px;
  min-height: 100%;
  padding: 56px 7vw;
  align-items: center;
  gap: 56px;
  overflow: hidden;
  background:
    linear-gradient(90deg, rgba(10, 39, 46, 0.72) 0%, rgba(10, 39, 46, 0.36) 46%, rgba(248, 251, 248, 0.94) 74%, #f8fbf8 100%),
    url("../assets/images/property-login-bg.jpg") center / cover no-repeat;
}

.login::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255,255,255,0) 0%, rgba(13, 71, 71, 0.16) 100%);
  pointer-events: none;
}

.login-visual {
  position: relative;
  z-index: 1;
  min-height: 520px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: #ffffff;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 700;
}

.brand-mark img {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.18);
  padding: 5px;
}

.visual-copy {
  max-width: 560px;
}

.eyebrow {
  margin: 0 0 14px;
  color: #b8f1df;
  font-size: 14px;
  font-weight: 700;
  text-transform: uppercase;
}

.visual-copy h1 {
  margin: 0;
  font-size: 44px;
  line-height: 1.18;
  font-weight: 800;
}

.summary {
  margin: 18px 0 0;
  max-width: 440px;
  color: rgba(255, 255, 255, 0.82);
  font-size: 16px;
  line-height: 1.8;
}

.process-strip {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.process-strip span {
  padding: 9px 14px;
  border: 1px solid rgba(255, 255, 255, 0.28);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.13);
  color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}

.login-form {
  position: relative;
  z-index: 1;
  width: 430px;
  padding: 38px 36px 28px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.74);
  box-shadow: 0 24px 70px rgba(22, 52, 52, 0.18);
  backdrop-filter: blur(18px);

  :deep(.el-input) {
    height: 44px;
  }

  :deep(.el-input__wrapper) {
    min-height: 44px;
    border-radius: 10px;
    background: #f9fbfa;
    box-shadow: 0 0 0 1px #dbe7e1 inset;
  }

  .input-icon {
    height: 44px;
    width: 15px;
    margin-left: 4px;
    color: #6b8279;
  }
}

.form-heading {
  margin-bottom: 28px;
}

.form-heading p {
  margin: 0 0 8px;
  color: #5f776f;
  font-size: 14px;
}

.form-heading h2 {
  margin: 0;
  color: #172d2a;
  font-size: 28px;
  font-weight: 800;
}

.code-input {
  width: 64%;
}

.login-code {
  width: 32%;
  height: 44px;
  float: right;
  border: 1px solid #dbe7e1;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;

  img {
    width: 100%;
    cursor: pointer;
    vertical-align: middle;
  }
}

.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 2px 0 22px;
}

.password-options {
  display: inline-flex;
  align-items: center;
  gap: 18px;
  color: #3c6258;
}

.forgot-password {
  padding: 0;
  border: 0;
  background: none;
  color: #3c6258;
  font-size: 14px;
  line-height: 22px;
  font-weight: 400;
  cursor: pointer;
}

.forgot-password:hover,
.forgot-password:focus-visible {
  color: #1b5b49;
  text-decoration: underline;
}

:deep(.password-options .el-checkbox__label) {
  font-size: 14px;
  color: #3c6258;
}

:global(.service-center-dialog .el-dialog__title) {
  color: #172d2a;
  font-weight: 600;
}

:global(.service-center-dialog) {
  max-width: calc(100vw - 32px);
}

.service-center-content {
  min-height: 260px;
}

.service-center-intro {
  margin: 0 0 20px;
  color: #5f776f;
  line-height: 1.6;
}

.contact-error {
  margin: 0 0 12px;
  color: #b84b3a;
}

.contact-list {
  margin: 0;
}

.contact-list > div {
  display: grid;
  grid-template-columns: 84px minmax(0, 1fr);
  gap: 12px;
  padding: 11px 0;
  border-top: 1px solid #e5eeea;
  line-height: 1.6;
}

.contact-list dt {
  color: #60756e;
}

.contact-list dd {
  margin: 0;
  color: #172d2a;
  overflow-wrap: anywhere;
}

.login-action {
  margin-bottom: 0;

  .el-button {
    width: 100%;
    height: 44px;
    border-radius: 10px;
    font-weight: 700;
  }
}

.el-login-footer {
  position: fixed;
  z-index: 1;
  bottom: 16px;
  left: 0;
  width: 100%;
  text-align: center;
  color: rgba(255, 255, 255, 0.78);
  font-size: 12px;
}

.login-code-img {
  height: 44px;
}

@media screen and (max-width: 960px) {
  .login {
    grid-template-columns: 1fr;
    padding: 30px 20px 72px;
    background:
      linear-gradient(180deg, rgba(10, 39, 46, 0.72) 0%, rgba(248, 251, 248, 0.92) 58%, #f8fbf8 100%),
      url("../assets/images/property-login-bg.jpg") center / cover no-repeat;
  }

  .login-visual {
    min-height: 300px;
  }

  .visual-copy h1 {
    font-size: 32px;
  }

  .login-form {
    width: 100%;
    max-width: 430px;
    margin: 0 auto;
  }
}
</style>
