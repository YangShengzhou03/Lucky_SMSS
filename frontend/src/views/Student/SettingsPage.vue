<template>
  <div class="settings-page">
    <!-- 设置导航栏 -->
    <div class="settings-sidebar">
      <div class="sidebar-header">
        <h2>系统设置</h2>
      </div>
      
      <el-menu
        :default-active="activeTab"
        class="el-menu-vertical"
        router
        @open="handleOpen"
        @close="handleClose"
      >
        <el-menu-item index="profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
        <el-menu-item index="password">
          <el-icon><Lock /></el-icon>
          <span>密码修改</span>
        </el-menu-item>
        <el-menu-item index="notifications">
          <el-icon><Bell /></el-icon>
          <span>通知设置</span>
        </el-menu-item>
        <el-menu-item index="privacy">
          <el-icon><Shield /></el-icon>
          <span>隐私设置</span>
        </el-menu-item>
        <el-menu-item index="theme">
          <el-icon><Brush /></el-icon>
          <span>主题设置</span>
        </el-menu-item>
        <el-menu-item index="security">
          <el-icon><SafetyCertificate /></el-icon>
          <span>安全设置</span>
        </el-menu-item>
        <el-menu-item index="bankcard">
          <el-icon><Wallet /></el-icon>
          <span>学校银行卡</span>
        </el-menu-item>
        <el-menu-item index="address">
          <el-icon><Location /></el-icon>
          <span>家庭住址</span>
        </el-menu-item>
        <el-menu-item index="emergency">
          <el-icon><Help /></el-icon>
          <span>紧急联系人</span>
        </el-menu-item>
        <el-menu-item index="organization">
          <el-icon><OfficeBuilding /></el-icon>
          <span>组织关系</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 设置内容区域 -->
    <div class="settings-content">
      <!-- 个人信息设置 -->
      <div v-show="activeTab === 'profile'" class="setting-panel">
        <div class="panel-header">
          <h3>个人信息</h3>
          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="small" 
              @click="saveProfile"
            >
              保存修改
            </el-button>
          </div>
        </div>
        
        <el-form :model="userProfile" label-width="120px" class="profile-form">
          <el-form-item label="学号">
            <el-input v-model="userProfile.studentId" disabled />
          </el-form-item>
          
          <el-form-item label="姓名">
            <el-input v-model="userProfile.name" />
          </el-form-item>
          
          <el-form-item label="性别">
            <el-radio-group v-model="userProfile.gender">
              <el-radio label="male">男</el-radio>
              <el-radio label="female">女</el-radio>
              <el-radio label="other">其他</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="出生日期">
            <el-date-picker
              v-model="userProfile.birthdate"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item label="学院">
            <el-input v-model="userProfile.college" disabled />
          </el-form-item>
          
          <el-form-item label="专业">
            <el-input v-model="userProfile.major" disabled />
          </el-form-item>
          
          <el-form-item label="年级">
            <el-input v-model="userProfile.grade" disabled />
          </el-form-item>
          
          <el-form-item label="邮箱">
            <el-input v-model="userProfile.email" />
          </el-form-item>
          
          <el-form-item label="手机号码">
            <el-input v-model="userProfile.phone" />
          </el-form-item>
          
          <el-form-item label="个人简介">
            <el-input 
              type="textarea" 
              v-model="userProfile.bio" 
              :rows="4"
            />
          </el-form-item>
          
          <el-form-item label="头像">
            <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="userProfile.avatarUrl" :src="userProfile.avatarUrl" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus />
              </el-icon>
            </el-upload>
          </el-form-item>
        </el-form>
      </div>

      <!-- 密码修改设置 -->
      <div v-show="activeTab === 'password'" class="setting-panel">
        <div class="panel-header">
          <h3>密码修改</h3>
        </div>
        
        <el-form 
          :model="passwordForm" 
          label-width="150px" 
          class="password-form"
          :rules="passwordRules"
          ref="passwordFormRef"
        >
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input 
              type="password" 
              v-model="passwordForm.oldPassword" 
              autocomplete="off"
            />
          </el-form-item>
          
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              type="password" 
              v-model="passwordForm.newPassword" 
              autocomplete="off"
            />
          </el-form-item>
          
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input 
              type="password" 
              v-model="passwordForm.confirmPassword" 
              autocomplete="off"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="changePassword"
            >
              修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 通知设置 -->
      <div v-show="activeTab === 'notifications'" class="setting-panel">
        <div class="panel-header">
          <h3>通知设置</h3>
        </div>
        
        <el-form :model="notificationSettings" label-width="150px" class="notification-form">
          <el-form-item label="消息通知">
            <el-switch 
              v-model="notificationSettings.message" 
              active-text="开启" 
              inactive-text="关闭"
            />
          </el-form-item>
          
          <el-form-item label="邮件通知">
            <el-switch 
              v-model="notificationSettings.email" 
              active-text="开启" 
              inactive-text="关闭"
            />
          </el-form-item>
          
          <el-form-item label="短信通知">
            <el-switch 
              v-model="notificationSettings.sms" 
              active-text="开启" 
              inactive-text="关闭"
            />
          </el-form-item>
          
          <el-form-item label="通知铃声">
            <el-switch 
              v-model="notificationSettings.sound" 
              active-text="开启" 
              inactive-text="关闭"
            />
          </el-form-item>
          
          <el-form-item label="震动提醒">
            <el-switch 
              v-model="notificationSettings.vibrate" 
              active-text="开启" 
              inactive-text="关闭"
            />
          </el-form-item>
          
          <el-form-item label="锁屏通知">
            <el-switch 
              v-model="notificationSettings.lockScreen" 
              active-text="开启" 
              inactive-text="关闭"
            />
          </el-form-item>
          
          <el-form-item label="通知分类">
            <el-checkbox-group v-model="notificationSettings.categories">
              <el-checkbox label="course">课程提醒</el-checkbox>
              <el-checkbox label="grade">成绩通知</el-checkbox>
              <el-checkbox label="library">图书逾期</el-checkbox>
              <el-checkbox label="announcement">系统公告</el-checkbox>
              <el-checkbox label="activity">活动邀请</el-checkbox>
              <el-checkbox label="payment">缴费提醒</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          
          <el-form-item label="通知时段">
            <el-time-select
              v-model="notificationSettings.startTime"
              placeholder="开始时间"
              arrow-control
            />
            <span class="time-separator">至</span>
            <el-time-select
              v-model="notificationSettings.endTime"
              placeholder="结束时间"
              arrow-control
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 隐私设置 -->
      <div v-show="activeTab === 'privacy'" class="setting-panel">
        <div class="panel-header">
          <h3>隐私设置</h3>
        </div>
        
        <el-form :model="privacySettings" label-width="150px" class="privacy-form">
          <el-form-item label="个人资料可见性">
            <el-radio-group v-model="privacySettings.profileVisibility">
              <el-radio label="all">所有人可见</el-radio>
              <el-radio label="classmates">仅同班同学可见</el-radio>
              <el-radio label="college">仅学院内可见</el-radio>
              <el-radio label="private">仅自己可见</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="课程表可见性">
            <el-radio-group v-model="privacySettings.scheduleVisibility">
              <el-radio label="all">所有人可见</el-radio>
              <el-radio label="classmates">仅同班同学可见</el-radio>
              <el-radio label="college">仅学院内可见</el-radio>
              <el-radio label="private">仅自己可见</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="成绩可见性">
            <el-radio-group v-model="privacySettings.gradeVisibility">
              <el-radio label="all">所有人可见</el-radio>
              <el-radio label="classmates">仅同班同学可见</el-radio>
              <el-radio label="college">仅学院内可见</el-radio>
              <el-radio label="private">仅自己可见</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="图书借阅记录">
            <el-radio-group v-model="privacySettings.borrowHistoryVisibility">
              <el-radio label="all">所有人可见</el-radio>
              <el-radio label="classmates">仅同班同学可见</el-radio>
              <el-radio label="college">仅学院内可见</el-radio>
              <el-radio label="private">仅自己可见</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="允许添加好友">
            <el-radio-group v-model="privacySettings.friendRequests">
              <el-radio label="all">所有人</el-radio>
              <el-radio label="classmates">仅同班同学</el-radio>
              <el-radio label="college">仅学院内同学</el-radio>
              <el-radio label="nobody">禁止</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="位置信息">
            <el-switch 
              v-model="privacySettings.locationSharing" 
              active-text="共享" 
              inactive-text="不共享"
            />
          </el-form-item>
          
          <el-form-item label="在线状态">
            <el-switch 
              v-model="privacySettings.onlineStatus" 
              active-text="显示" 
              inactive-text="隐藏"
            />
          </el-form-item>
          
          <el-form-item label="数据导出">
            <el-button 
              type="primary" 
              size="small" 
              @click="exportData"
            >
              导出个人数据
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 主题设置 -->
      <div v-show="activeTab === 'theme'" class="setting-panel">
        <div class="panel-header">
          <h3>主题设置</h3>
        </div>
        
        <el-form :model="themeSettings" label-width="150px" class="theme-form">
          <el-form-item label="主题模式">
            <el-radio-group v-model="themeSettings.themeMode">
              <el-radio label="light">浅色模式</el-radio>
              <el-radio label="dark">深色模式</el-radio>
              <el-radio label="auto">自动模式</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="主色调">
            <el-color-picker 
              v-model="themeSettings.primaryColor" 
              show-alpha
            />
          </el-form-item>
          
          <el-form-item label="强调色">
            <el-color-picker 
              v-model="themeSettings.accentColor" 
              show-alpha
            />
          </el-form-item>
          
          <el-form-item label="背景毛玻璃效果">
            <el-slider 
              v-model="themeSettings.blurIntensity" 
              :min="0" 
              :max="20" 
              :step="1"
              show-tooltip
              format-tooltip="(val) => `${val}px`"
            />
          </el-form-item>
          
          <el-form-item label="背景透明度">
            <el-slider 
              v-model="themeSettings.bgOpacity" 
              :min="0.5" 
              :max="1" 
              :step="0.05"
              show-tooltip
              format-tooltip="(val) => `${Math.round(val * 100)}%`"
            />
          </el-form-item>
          
          <el-form-item label="预设主题">
            <div class="theme-presets">
              <div 
                class="theme-preset" 
                v-for="(preset, index) in themePresets" 
                :key="index"
                :style="{ backgroundColor: preset.bgColor }"
                @click="applyThemePreset(preset)"
                :class="{ active: isThemePresetActive(preset) }"
              >
                <div class="preset-color" :style="{ backgroundColor: preset.primaryColor }"></div>
                <div class="preset-name">{{ preset.name }}</div>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <!-- 安全设置 -->
      <div v-show="activeTab === 'security'" class="setting-panel">
        <div class="panel-header">
          <h3>安全设置</h3>
        </div>
        
        <el-form :model="securitySettings" label-width="150px" class="security-form">
          <el-form-item label="两步验证">
            <el-switch 
              v-model="securitySettings.twoFactorAuth" 
              active-text="开启" 
              inactive-text="关闭"
            />
          </el-form-item>
          
          <el-form-item label="验证方式">
            <el-radio-group v-model="securitySettings.authMethod" v-if="securitySettings.twoFactorAuth">
              <el-radio label="email">邮箱验证码</el-radio>
              <el-radio label="sms">短信验证码</el-radio>
              <el-radio label="app">身份验证器</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="登录保护">
            <el-checkbox-group v-model="securitySettings.loginProtection">
              <el-checkbox label="email">邮箱通知</el-checkbox>
              <el-checkbox label="sms">短信通知</el-checkbox>
              <el-checkbox label="block">异地登录阻止</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          
          <el-form-item label="最近登录记录">
            <el-table 
              :data="loginHistory" 
              border 
              stripe
              class="history-table"
            >
              <el-table-column prop="ip" label="IP地址" width="120" />
              <el-table-column prop="location" label="登录地点" width="150" />
              <el-table-column prop="time" label="登录时间" width="180" />
              <el-table-column prop="device" label="设备" width="150" />
              <el-table-column prop="status" label="状态" width="100" 
                :formatter="formatLoginStatus" />
            </el-table>
          </el-form-item>
          
          <el-form-item label="绑定邮箱">
            <div class="email-binding">
              <el-input v-model="securitySettings.email" disabled />
              <el-button 
                type="text" 
                size="small" 
                @click="changeEmail"
                :disabled="securitySettings.emailChanging"
              >
                {{ securitySettings.emailChanging ? '正在修改...' : '修改' }}
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item label="绑定手机">
            <div class="phone-binding">
              <el-input v-model="securitySettings.phone" disabled />
              <el-button 
                type="text" 
                size="small" 
                @click="changePhone"
                :disabled="securitySettings.phoneChanging"
              >
                {{ securitySettings.phoneChanging ? '正在修改...' : '修改' }}
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item label="会话管理">
            <el-button 
              type="danger" 
              size="small" 
              @click="logoutOtherDevices"
            >
              退出其他设备登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 学校银行卡设置 -->
      <div v-show="activeTab === 'bankcard'" class="setting-panel">
        <div class="panel-header">
          <h3>学校银行卡</h3>
          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="small" 
              @click="addBankCard"
              :disabled="bankCards.length >= 3"
            >
              添加银行卡
            </el-button>
          </div>
        </div>
        
        <div class="bank-cards-container">
          <el-card 
            class="bank-card" 
            v-for="(card, index) in bankCards" 
            :key="index"
            shadow="hover"
          >
            <div class="card-header">
              <div class="bank-logo" :style="{ backgroundColor: card.color }">
                <el-icon><Bank /></el-icon>
              </div>
              <div class="bank-info">
                <div class="bank-name">{{ card.bankName }}</div>
                <div class="card-type">{{ card.cardType }}</div>
              </div>
              <div class="card-actions">
                <el-button 
                  type="text" 
                  size="small" 
                  @click="editBankCard(card)"
                >
                  编辑
                </el-button>
                <el-button 
                  type="text" 
                  size="small" 
                  @click="deleteBankCard(card)"
                >
                  删除
                </el-button>
              </div>
            </div>
            
            <div class="card-number">
              **** **** **** {{ card.cardNumber.slice(-4) }}
            </div>
            
            <div class="card-footer">
              <div class="card-holder">持卡人: {{ card.cardHolder }}</div>
              <div class="default-tag" v-if="card.isDefault">默认卡</div>
              <div class="set-default" v-else>
                <el-button 
                  type="text" 
                  size="small" 
                  @click="setDefaultBankCard(card)"
                >
                  设置为默认
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
        
        <div class="bank-card-tips">
          <p>1. 支持绑定最多3张银行卡</p>
          <p>2. 默认卡将用于学费缴纳、奖学金发放等操作</p>
          <p>3. 请确保填写的银行卡信息准确无误</p>
        </div>
      </div>

      <!-- 家庭住址设置 -->
      <div v-show="activeTab === 'address'" class="setting-panel">
        <div class="panel-header">
          <h3>家庭住址</h3>
          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="small" 
              @click="addAddress"
            >
              添加地址
            </el-button>
          </div>
        </div>
        
        <el-form :model="addressForm" label-width="120px" class="address-form">
          <el-form-item label="省份/地区">
            <el-select v-model="addressForm.province" placeholder="请选择省份/地区">
              <el-option
                v-for="item in provinces"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="城市">
            <el-select v-model="addressForm.city" placeholder="请选择城市">
              <el-option
                v-for="item in getCitiesByProvince(addressForm.province)"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="区县">
            <el-select v-model="addressForm.district" placeholder="请选择区县">
              <el-option
                v-for="item in getDistrictsByCity(addressForm.city)"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="详细地址">
            <el-input 
              type="textarea" 
              v-model="addressForm.detail" 
              :rows="2"
            />
          </el-form-item>
          
          <el-form-item label="邮政编码">
            <el-input v-model="addressForm.postcode" />
          </el-form-item>
          
          <el-form-item label="联系人姓名">
            <el-input v-model="addressForm.contactName" />
          </el-form-item>
          
          <el-form-item label="联系电话">
            <el-input v-model="addressForm.contactPhone" />
          </el-form-item>
          
          <el-form-item label="是否默认">
            <el-switch 
              v-model="addressForm.isDefault" 
              active-text="是" 
              inactive-text="否"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="saveAddress"
            >
              保存地址
            </el-button>
            <el-button 
              type="text" 
              @click="cancelAddressEdit"
              v-if="isEditingAddress"
            >
              取消
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="address-list">
          <el-card 
            class="address-card" 
            v-for="(address, index) in addressList" 
            :key="index"
            shadow="hover"
          >
            <div class="address-info">
              <div class="address-header">
                <div class="contact-info">
                  <span class="contact-name">{{ address.contactName }}</span>
                  <span class="contact-phone">{{ address.contactPhone }}</span>
                </div>
                <div class="default-tag" v-if="address.isDefault">默认地址</div>
              </div>
              
              <div class="address-detail">
                {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail }}
              </div>
              
              <div class="address-footer">
                <div class="postcode">邮编: {{ address.postcode }}</div>
                <div class="address-actions">
                  <el-button 
                    type="text" 
                    size="small" 
                    @click="editAddress(address)"
                  >
                    编辑
                  </el-button>
                  <el-button 
                    type="text" 
                    size="small" 
                    @click="deleteAddress(address)"
                  >
                    删除
                  </el-button>
                  <el-button 
                    type="text" 
                    size="small" 
                    @click="setDefaultAddress(address)"
                    v-if="!address.isDefault"
                  >
                    设置为默认
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 紧急联系人设置 -->
      <div v-show="activeTab === 'emergency'" class="setting-panel">
        <div class="panel-header">
          <h3>紧急联系人</h3>
          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="small" 
              @click="addEmergencyContact"
              :disabled="emergencyContacts.length >= 3"
            >
              添加联系人
            </el-button>
          </div>
        </div>
        
        <el-form :model="emergencyContactForm" label-width="120px" class="emergency-form">
          <el-form-item label="姓名">
            <el-input v-model="emergencyContactForm.name" />
          </el-form-item>
          
          <el-form-item label="关系">
            <el-select v-model="emergencyContactForm.relationship" placeholder="请选择关系">
              <el-option label="父亲" value="father" />
              <el-option label="母亲" value="mother" />
              <el-option label="兄弟姐妹" value="sibling" />
              <el-option label="配偶" value="spouse" />
              <el-option label="其他亲属" value="relative" />
              <el-option label="朋友" value="friend" />
              <el-option label="老师" value="teacher" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="联系电话">
            <el-input v-model="emergencyContactForm.phone" />
          </el-form-item>
          
          <el-form-item label="备用电话">
            <el-input v-model="emergencyContactForm.alternatePhone" />
          </el-form-item>
          
          <el-form-item label="工作单位">
            <el-input v-model="emergencyContactForm.workUnit" />
          </el-form-item>
          
          <el-form-item label="备注信息">
            <el-input 
              type="textarea" 
              v-model="emergencyContactForm.notes" 
              :rows="2"
            />
          </el-form-item>
          
          <el-form-item label="是否主要联系人">
            <el-switch 
              v-model="emergencyContactForm.isPrimary" 
              active-text="是" 
              inactive-text="否"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="saveEmergencyContact"
            >
              保存联系人
            </el-button>
            <el-button 
              type="text" 
              @click="cancelEmergencyContactEdit"
              v-if="isEditingEmergencyContact"
            >
              取消
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="emergency-contacts-list">
          <el-card 
            class="emergency-contact-card" 
            v-for="(contact, index) in emergencyContacts" 
            :key="index"
            shadow="hover"
          >
            <div class="contact-header">
              <div class="contact-info">
                <div class="contact-name">{{ contact.name }}</div>
                <div class="contact-relationship">{{ getRelationshipText(contact.relationship) }}</div>
              </div>
              <div class="primary-tag" v-if="contact.isPrimary">主要联系人</div>
            </div>
            
            <div class="contact-details">
              <div class="contact-phone">
                <el-icon><Phone /></el-icon>
                <span>{{ contact.phone }}</span>
              </div>
              <div class="contact-alternate" v-if="contact.alternatePhone">
                <el-icon><Mobile /></el-icon>
                <span>{{ contact.alternatePhone }}</span>
              </div>
              <div class="contact-work" v-if="contact.workUnit">
                <el-icon><OfficeBuilding /></el-icon>
                <span>{{ contact.workUnit }}</span>
              </div>
            </div>
            
            <div class="contact-actions">
              <el-button 
                type="text" 
                size="small" 
                @click="editEmergencyContact(contact)"
              >
                编辑
              </el-button>
              <el-button 
                type="text" 
                size="small" 
                @click="deleteEmergencyContact(contact)"
              >
                删除
              </el-button>
              <el-button 
                type="text" 
                size="small" 
                @click="setPrimaryEmergencyContact(contact)"
                v-if="!contact.isPrimary"
              >
                设置为主要
              </el-button>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 组织关系设置 -->
      <div v-show="activeTab === 'organization'" class="setting-panel">
        <div class="panel-header">
          <h3>组织关系</h3>
        </div>
        
        <el-form :model="organizationInfo" label-width="150px" class="organization-form">
          <el-form-item label="政治面貌">
            <el-select v-model="organizationInfo.politicalStatus" placeholder="请选择政治面貌">
              <el-option label="中共党员" value="partyMember" />
              <el-option label="中共预备党员" value="probationaryPartyMember" />
              <el-option label="共青团员" value="communistYouthLeagueMember" />
              <el-option label="民主党派" value="democraticPartyMember" />
              <el-option label="无党派人士" value="nonPartyAffiliation" />
              <el-option label="群众" value="masses" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="入党/入团时间">
            <el-date-picker
              v-model="organizationInfo.partyJoinDate"
              type="date"
              placeholder="请选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item label="组织关系所在单位">
            <el-input v-model="organizationInfo.organizationUnit" />
          </el-form-item>
          
          <el-form-item label="组织关系状态">
            <el-select v-model="organizationInfo.organizationStatus" placeholder="请选择状态">
              <el-option label="正常" value="normal" />
              <el-option label="转出中" value="transferringOut" />
              <el-option label="转入中" value="transferringIn" />
              <el-option label="暂停" value="suspended" />
              <el-option label="退党/退团" value="withdrawn" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="最后一次组织生活时间">
            <el-date-picker
              v-model="organizationInfo.lastPartyMeetingDate"
              type="date"
              placeholder="请选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item label="党费/团费缴纳情况">
            <el-select v-model="organizationInfo.duesStatus" placeholder="请选择状态">
              <el-option label="已缴清" value="paid" />
              <el-option label="已缴至" value="paidTo" />
              <el-option label="未缴" value="unpaid" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="备注信息">
            <el-input 
              type="textarea" 
              v-model="organizationInfo.notes" 
              :rows="3"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="saveOrganizationInfo"
            >
              保存信息
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="organization-history">
          <h4>组织关系变更历史</h4>
          <el-timeline>
            <el-timeline-item 
              v-for="(history, index) in organizationHistory" 
              :key="index"
              :timestamp="history.date"
              placement="top"
            >
              <el-card shadow="hover">
                <div class="history-title">{{ history.title }}</div>
                <div class="history-details">{{ history.details }}</div>
                <div class="history-handler">处理人: {{ history.handler }}</div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { 
  User, Lock, Bell, Shield, Brush, SafetyCertificate, 
  Wallet, Location, Help, OfficeBuilding, Plus, 
  Bank, Phone, Mobile
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 当前激活的设置标签
const activeTab = ref('profile')

// 个人信息
const userProfile = reactive({
  studentId: '20230001',
  name: '张三',
  gender: 'male',
  birthdate: '2000-01-15',
  college: '计算机科学与技术学院',
  major: '软件工程',
  grade: '2023级',
  email: 'zhangsan@example.com',
  phone: '13800138000',
  bio: '热爱学习，喜欢编程和阅读',
  avatarUrl: 'https://picsum.photos/id/1005/200/200'
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const passwordRules = reactive({
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { 
      validator: (rule, value) => {
        if (value !== passwordForm.newPassword) {
          return Promise.reject('两次输入的密码不一致')
        } else {
          return Promise.resolve()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 通知设置
const notificationSettings = reactive({
  message: true,
  email: true,
  sms: false,
  sound: true,
  vibrate: true,
  lockScreen: true,
  categories: ['course', 'grade', 'announcement'],
  startTime: '08:00',
  endTime: '22:00'
})

// 隐私设置
const privacySettings = reactive({
  profileVisibility: 'classmates',
  scheduleVisibility: 'college',
  gradeVisibility: 'private',
  borrowHistoryVisibility: 'classmates',
  friendRequests: 'classmates',
  locationSharing: false,
  onlineStatus: true
})

// 主题设置
const themeSettings = reactive({
  themeMode: 'light',
  primaryColor: '#409eff',
  accentColor: '#67c23a',
  blurIntensity: 12,
  bgOpacity: 0.75
})

// 主题预设
const themePresets = [
  { name: '蓝色经典', primaryColor: '#409eff', bgColor: '#f5f7fa', mode: 'light' },
  { name: '绿色清新', primaryColor: '#67c23a', bgColor: '#f0f9eb', mode: 'light' },
  { name: '橙色活力', primaryColor: '#e6a23c', bgColor: '#fdf6ec', mode: 'light' },
  { name: '紫色优雅', primaryColor: '#9d5cd6', bgColor: '#f3f0ff', mode: 'light' },
  { name: '深色模式', primaryColor: '#409eff', bgColor: '#1e1e1e', mode: 'dark' }
]

// 安全设置
const securitySettings = reactive({
  twoFactorAuth: false,
  authMethod: 'email',
  loginProtection: ['email', 'sms'],
  email: 'zhangsan@example.com',
  phone: '13800138000',
  emailChanging: false,
  phoneChanging: false
})

// 登录历史
const loginHistory = [
  { ip: '192.168.1.100', location: '学校图书馆', time: '2023-06-15 09:30', device: 'Chrome/Windows', status: 'success' },
  { ip: '10.0.0.5', location: '宿舍', time: '2023-06-14 21:45', device: 'Firefox/MacOS', status: 'success' },
  { ip: '220.181.38.148', location: '校外', time: '2023-06-12 14:20', device: 'Safari/iOS', status: 'success' },
  { ip: '117.136.32.135', location: '未知', time: '2023-06-10 08:15', device: 'IE/Windows', status: 'failed' }
]

// 学校银行卡
const bankCards = reactive([
  {
    id: 1,
    bankName: '中国工商银行',
    cardType: '借记卡',
    cardNumber: '6222 0212 3456 7890',
    cardHolder: '张三',
    color: '#e6a23c',
    isDefault: true
  },
  {
    id: 2,
    bankName: '中国建设银行',
    cardType: '校园卡',
    cardNumber: '6217 0000 1000 0000',
    cardHolder: '张三',
    color: '#409eff',
    isDefault: false
  }
])

// 地址数据
const addressList = reactive([
  {
    id: 1,
    province: '北京市',
    city: '北京市',
    district: '海淀区',
    detail: '清华大学学生公寓1号楼101室',
    postcode: '100084',
    contactName: '张三',
    contactPhone: '13800138000',
    isDefault: true
  },
  {
    id: 2,
    province: '上海市',
    city: '上海市',
    district: '浦东新区',
    detail: '张江高科技园区博云路2号浦软大厦12层',
    postcode: '201203',
    contactName: '张父',
    contactPhone: '13900139000',
    isDefault: false
  }
])

// 省份数据
const provinces = [
  { value: '北京市', label: '北京市' },
  { value: '上海市', label: '上海市' },
  { value: '天津市', label: '天津市' },
  { value: '重庆市', label: '重庆市' },
  { value: '河北省', label: '河北省' },
  { value: '山西省', label: '山西省' },
  { value: '辽宁省', label: '辽宁省' },
  { value: '吉林省', label: '吉林省' },
  { value: '黑龙江省', label: '黑龙江省' },
  { value: '江苏省', label: '江苏省' },
  { value: '浙江省', label: '浙江省' },
  { value: '安徽省', label: '安徽省' },
  { value: '福建省', label: '福建省' },
  { value: '江西省', label: '江西省' },
  { value: '山东省', label: '山东省' },
  { value: '河南省', label: '河南省' },
  { value: '湖北省', label: '湖北省' },
  { value: '湖南省', label: '湖南省' },
  { value: '广东省', label: '广东省' },
  { value: '海南省', label: '海南省' },
  { value: '四川省', label: '四川省' },
  { value: '贵州省', label: '贵州省' },
  { value: '云南省', label: '云南省' },
  { value: '陕西省', label: '陕西省' },
  { value: '甘肃省', label: '甘肃省' },
  { value: '青海省', label: '青海省' },
  { value: '台湾省', label: '台湾省' },
  { value: '内蒙古自治区', label: '内蒙古自治区' },
  { value: '广西壮族自治区', label: '广西壮族自治区' },
  { value: '西藏自治区', label: '西藏自治区' },
  { value: '宁夏回族自治区', label: '宁夏回族自治区' },
  { value: '新疆维吾尔自治区', label: '新疆维吾尔自治区' },
  { value: '香港特别行政区', label: '香港特别行政区' },
  { value: '澳门特别行政区', label: '澳门特别行政区' }
]

// 城市数据 (简化版)
const cities = {
  '北京市': [
    { value: '北京市', label: '北京市' }
  ],
  '上海市': [
    { value: '上海市', label: '上海市' }
  ],
  '广东省': [
    { value: '广州市', label: '广州市' },
    { value: '深圳市', label: '深圳市' },
    { value: '佛山市', label: '佛山市' }
  ]
  // 其他省份城市数据省略...
}

// 区县数据 (简化版)
const districts = {
  '北京市': {
    '北京市': [
      { value: '东城区', label: '东城区' },
      { value: '西城区', label: '西城区' },
      { value: '海淀区', label: '海淀区' },
      { value: '朝阳区', label: '朝阳区' }
    ]
  },
  '上海市': {
    '上海市': [
      { value: '黄浦区', label: '黄浦区' },
      { value: '徐汇区', label: '徐汇区' },
      { value: '浦东新区', label: '浦东新区' },
      { value: '静安区', label: '静安区' }
    ]
  },
  '广东省': {
    '广州市': [
      { value: '天河区', label: '天河区' },
      { value: '越秀区', label: '越秀区' },
      { value: '海珠区', label: '海珠区' }
    ],
    '深圳市': [
      { value: '南山区', label: '南山区' },
      { value: '福田区', label: '福田区' },
      { value: '宝安区', label: '宝安区' }
    ]
  }
  // 其他城市区县数据省略...
}

// 紧急联系人
const emergencyContacts = reactive([
  {
    id: 1,
    name: '张父',
    relationship: 'father',
    phone: '13900139000',
    alternatePhone: '13800138001',
    workUnit: 'XX科技有限公司',
    notes: '父亲，工作稳定，随时可联系',
    isPrimary: true
  },
  {
    id: 2,
    name: '李老师',
    relationship: 'teacher',
    phone: '13700137000',
    alternatePhone: '',
    workUnit: '清华大学计算机系',
    notes: '导师，学术问题可联系',
    isPrimary: false
  }
])

// 组织关系
const organizationInfo = reactive({
  politicalStatus: 'communistYouthLeagueMember',
  partyJoinDate: '2018-05-04',
  organizationUnit: '清华大学计算机系学生团支部',
  organizationStatus: 'normal',
  lastPartyMeetingDate: '2023-06-10',
  duesStatus: 'paid',
  notes: '积极参加组织活动，按时缴纳团费'
})

// 组织关系历史
const organizationHistory = [
  {
    date: '2023-06-10',
    title: '参加计算机系学生团支部主题团日活动',
    details: '学习二十大精神，交流学习心得',
    handler: '王书记'
  },
  {
    date: '2023-03-05',
    title: '团组织关系转入清华大学',
    details: '从XX中学转入',
    handler: '李老师'
  },
  {
    date: '2018-05-04',
    title: '加入中国共产主义青年团',
    details: '在XX中学加入共青团',
    handler: '张老师'
  }
]

// 地址表单
const addressForm = reactive({
  id: null,
  province: '',
  city: '',
  district: '',
  detail: '',
  postcode: '',
  contactName: '',
  contactPhone: '',
  isDefault: false
})

// 是否正在编辑地址
const isEditingAddress = ref(false)

// 紧急联系人表单
const emergencyContactForm = reactive({
  id: null,
  name: '',
  relationship: 'father',
  phone: '',
  alternatePhone: '',
  workUnit: '',
  notes: '',
  isPrimary: false
})

// 是否正在编辑紧急联系人
const isEditingEmergencyContact = ref(false)

// 方法
const handleOpen = (key, keyPath) => {
  console.log(key, keyPath)
}

const handleClose = (key, keyPath) => {
  console.log(key, keyPath)
}

// 个人信息相关方法
const saveProfile = () => {
  // 模拟保存个人信息
  ElMessage.success('个人信息保存成功')
}

const handleAvatarSuccess = (res, file) => {
  userProfile.avatarUrl = URL.createObjectURL(file.raw)
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 密码修改相关方法
const changePassword = () => {
  // 模拟修改密码
  ElMessage.success('密码修改成功，请使用新密码登录')
}

// 安全设置相关方法
const changeEmail = () => {
  securitySettings.emailChanging = true
  // 模拟修改邮箱流程
  setTimeout(() => {
    securitySettings.emailChanging = false
    ElMessage.success('邮箱修改成功')
  }, 2000)
}

const changePhone = () => {
  securitySettings.phoneChanging = true
  // 模拟修改手机号流程
  setTimeout(() => {
    securitySettings.phoneChanging = false
    ElMessage.success('手机号修改成功')
  }, 2000)
}

const logoutOtherDevices = () => {
  ElMessageBox.confirm(
    '确定要退出其他设备的登录吗？您将需要在其他设备上重新登录。',
    '确认退出',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 模拟退出其他设备登录
    ElMessage.success('已退出其他设备登录')
  }).catch(() => {
    ElMessage.info('已取消')
  })
}

const formatLoginStatus = (row) => {
  return row.status === 'success' ? '成功' : '失败'
}

// 学校银行卡相关方法
const addBankCard = () => {
  // 模拟添加银行卡
  ElMessage.success('银行卡添加成功')
}

const editBankCard = (card) => {
  // 模拟编辑银行卡
  ElMessage.info('编辑银行卡: ' + card.bankName)
}

const deleteBankCard = (card) => {
  ElMessageBox.confirm(
    `确定要删除这张${card.bankName}的银行卡吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 模拟删除银行卡
    ElMessage.success('银行卡删除成功')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const setDefaultBankCard = (card) => {
  // 模拟设置默认银行卡
  bankCards.forEach(item => {
    item.isDefault = item.id === card.id
  })
  ElMessage.success('已设置为默认银行卡')
}

// 地址相关方法
const addAddress = () => {
  // 重置表单
  addressForm.id = null
  addressForm.province = ''
  addressForm.city = ''
  addressForm.district = ''
  addressForm.detail = ''
  addressForm.postcode = ''
  addressForm.contactName = ''
  addressForm.contactPhone = ''
  addressForm.isDefault = false
  
  isEditingAddress.value = true
}

const editAddress = (address) => {
  // 填充表单
  addressForm.id = address.id
  addressForm.province = address.province
  addressForm.city = address.city
  addressForm.district = address.district
  addressForm.detail = address.detail
  addressForm.postcode = address.postcode
  addressForm.contactName = address.contactName
  addressForm.contactPhone = address.contactPhone
  addressForm.isDefault = address.isDefault
  
  isEditingAddress.value = true
}

const saveAddress = () => {
  // 模拟保存地址
  ElMessage.success('地址保存成功')
  isEditingAddress.value = false
}

const cancelAddressEdit = () => {
  isEditingAddress.value = false
}

const setDefaultAddress = (address) => {
  // 模拟设置默认地址
  addressList.forEach(item => {
    item.isDefault = item.id === address.id
  })
  ElMessage.success('已设置为默认地址')
}

const getCitiesByProvince = (province) => {
  return cities[province] || []
}

const getDistrictsByCity = (city) => {
  const province = addressForm.province
  if (!province || !cities[province] || !cities[province].some(item => item.value === city)) {
    return []
  }
  return districts[province]?.[city] || []
}

// 紧急联系人相关方法
const addEmergencyContact = () => {
  // 重置表单
  emergencyContactForm.id = null
  emergencyContactForm.name = ''
  emergencyContactForm.relationship = 'father'
  emergencyContactForm.phone = ''
  emergencyContactForm.alternatePhone = ''
  emergencyContactForm.workUnit = ''
  emergencyContactForm.notes = ''
  emergencyContactForm.isPrimary = false
  
  isEditingEmergencyContact.value = true
}

const editEmergencyContact = (contact) => {
  // 填充表单
  emergencyContactForm.id = contact.id
  emergencyContactForm.name = contact.name
  emergencyContactForm.relationship = contact.relationship
  emergencyContactForm.phone = contact.phone
  emergencyContactForm.alternatePhone = contact.alternatePhone
  emergencyContactForm.workUnit = contact.workUnit
  emergencyContactForm.notes = contact.notes
  emergencyContactForm.isPrimary = contact.isPrimary
  
  isEditingEmergencyContact.value = true
}

const deleteEmergencyContact = (contact) => {
  ElMessageBox.confirm(
    `确定要删除紧急联系人${contact.name}吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 模拟删除紧急联系人
    ElMessage.success('紧急联系人删除成功')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const saveEmergencyContact = () => {
  // 模拟保存紧急联系人
  ElMessage.success('紧急联系人保存成功')
  isEditingEmergencyContact.value = false
}

const cancelEmergencyContactEdit = () => {
  isEditingEmergencyContact.value = false
}

const setPrimaryEmergencyContact = (contact) => {
  // 模拟设置主要紧急联系人
  emergencyContacts.forEach(item => {
    item.isPrimary = item.id === contact.id
  })
  ElMessage.success('已设置为主要紧急联系人')
}

const getRelationshipText = (relationship) => {
  const relationships = {
    'father': '父亲',
    'mother': '母亲',
    'sibling': '兄弟姐妹',
    'spouse': '配偶',
    'relative': '其他亲属',
    'friend': '朋友',
    'teacher': '老师'
  }
  return relationships[relationship] || relationship
}

// 组织关系相关方法
const saveOrganizationInfo = () => {
  // 模拟保存组织关系信息
  ElMessage.success('组织关系信息保存成功')
}

// 主题相关方法
const applyThemePreset = (preset) => {
  themeSettings.themeMode = preset.mode
  themeSettings.primaryColor = preset.primaryColor
  ElMessage.success(`已应用${preset.name}`)
}

const isThemePresetActive = (preset) => {
  return themeSettings.themeMode === preset.mode && 
         themeSettings.primaryColor === preset.primaryColor
}

// 导出个人数据
const exportData = () => {
  ElMessageBox.confirm(
    '确定要导出您的个人数据吗？这将生成一份包含您所有个人信息的文件。',
    '确认导出',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    // 模拟导出个人数据
    ElMessage.success('个人数据导出成功，正在下载...')
  }).catch(() => {
    ElMessage.info('已取消导出')
  })
}

// 初始化
onMounted(() => {
  // 模拟从服务器加载数据
})
</script>

<style scoped lang="scss">
.settings-page {
  display: flex;
  min-height: calc(100vh - 60px);
  padding: 20px;
  background-color: #f5f7fa;
}

.settings-sidebar {
  width: 220px;
  margin-right: 20px;
  
  .sidebar-header {
    padding: 20px;
    background: rgba(255, 255, 255, 0.75);
    backdrop-filter: blur(12px);
    border-radius: 12px 12px 0 0;
    border: 1px solid rgba(255, 255, 255, 0.4);
    
    h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }
  }
  
  .el-menu-vertical {
    border-right: none;
    background: rgba(255, 255, 255, 0.75);
    backdrop-filter: blur(12px);
    border-radius: 0 0 12px 12px;
    border: 1px solid rgba(255, 255, 255, 0.4);
  }
}

.settings-content {
  flex: 1;
  
  .setting-panel {
    background: rgba(255, 255, 255, 0.75);
    backdrop-filter: blur(12px);
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 24px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.4);
    
    .panel-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }
      
      .action-buttons {
        display: flex;
        gap: 10px;
      }
    }
  }
  
  .profile-form, .password-form, .notification-form, .privacy-form, 
  .theme-form, .security-form, .bank-cards-container, .address-form, 
  .address-list, .emergency-form, .emergency-contacts-list, 
  .organization-form, .organization-history {
    margin-top: 16px;
  }
  
  .avatar-uploader {
    .avatar {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      object-fit: cover;
      cursor: pointer;
    }
    
    .avatar-uploader-icon {
      width: 120px;
      height: 120px;
      line-height: 120px;
      text-align: center;
      border: 1px dashed #d9d9d9;
      border-radius: 50%;
      background-color: #fafafa;
      color: #8c8c8c;
      font-size: 28px;
      cursor: pointer;
      transition: border-color 0.3s;
      
      &:hover {
        border-color: #409eff;
      }
    }
  }
  
  .theme-presets {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    
    .theme-preset {
      width: 120px;
      height: 80px;
      border-radius: 8px;
      padding: 8px;
      cursor: pointer;
      transition: transform 0.3s, box-shadow 0.3s;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      
      .preset-color {
        height: 30px;
        border-radius: 4px;
      }
      
      .preset-name {
        font-size: 12px;
        color: #333;
        text-align: center;
      }
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      }
      
      &.active {
        border: 2px solid #409eff;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }
    }
  }
  
  .bank-cards-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 16px;
    
    .bank-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        
        .bank-logo {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          display: flex;
          justify-content: center;
          align-items: center;
          color: white;
          margin-right: 12px;
        }
        
        .bank-info {
          flex: 1;
          
          .bank-name {
            font-size: 16px;
            font-weight: 500;
            color: #333;
          }
          
          .card-type {
            font-size: 12px;
            color: #666;
          }
        }
        
        .card-actions {
          display: flex;
          gap: 8px;
        }
      }
      
      .card-number {
        font-size: 18px;
        font-family: monospace;
        color: #333;
        margin-bottom: 12px;
      }
      
      .card-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .card-holder {
          font-size: 14px;
          color: #666;
        }
        
        .default-tag {
          background-color: #409eff;
          color: white;
          font-size: 12px;
          padding: 2px 6px;
          border-radius: 4px;
        }
      }
    }
  }
  
  .bank-card-tips {
    margin-top: 16px;
    padding: 12px;
    background-color: #f5f7fa;
    border-radius: 8px;
    
    p {
      margin: 4px 0;
      font-size: 14px;
      color: #666;
    }
  }
  
  .address-list {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 16px;
    
    .address-card {
      .address-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
        
        .contact-info {
          .contact-name {
            font-size: 16px;
            font-weight: 500;
            color: #333;
            margin-right: 8px;
          }
          
          .contact-phone {
            font-size: 14px;
            color: #666;
          }
        }
        
        .default-tag {
          background-color: #409eff;
          color: white;
          font-size: 12px;
          padding: 2px 6px;
          border-radius: 4px;
        }
      }
      
      .address-detail {
        font-size: 14px;
        color: #333;
        margin-bottom: 8px;
      }
      
      .address-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .postcode {
          font-size: 12px;
          color: #666;
        }
        
        .address-actions {
          display: flex;
          gap: 8px;
        }
      }
    }
  }
  
  .emergency-contacts-list {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 16px;
    
    .emergency-contact-card {
      .contact-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        
        .contact-info {
          .contact-name {
            font-size: 16px;
            font-weight: 500;
            color: #333;
          }
          
          .contact-relationship {
            font-size: 12px;
            color: #666;
          }
        }
        
        .primary-tag {
          background-color: #67c23a;
          color: white;
          font-size: 12px;
          padding: 2px 6px;
          border-radius: 4px;
        }
      }
      
      .contact-details {
        margin-bottom: 12px;
        
        > div {
          display: flex;
          align-items: center;
          margin-bottom: 6px;
          
          el-icon {
            margin-right: 6px;
            color: #909399;
          }
          
          span {
            font-size: 14px;
            color: #333;
          }
        }
      }
      
      .contact-actions {
        display: flex;
        gap: 8px;
      }
    }
  }
  
  .organization-history {
    .history-title {
      font-size: 16px;
      font-weight: 500;
      color: #333;
      margin-bottom: 6px;
    }
    
    .history-details {
      font-size: 14px;
      color: #666;
      margin-bottom: 6px;
    }
    
    .history-handler {
      font-size: 12px;
      color: #909399;
    }
  }
}

.time-separator {
  margin: 0 8px;
  color: #909399;
}
</style>