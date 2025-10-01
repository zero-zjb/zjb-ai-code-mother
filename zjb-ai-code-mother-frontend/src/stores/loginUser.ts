import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import {getLoginUser} from "@/api/userController.ts";

export const useLoginUserStore = defineStore('loginUser', () => {

  // 登录用户
  const loginUser = ref<API.LoginUserVO>({
    userName: ''
  })

  // 获取登录用户
  async function fetchLoginUser() {
    const res = await getLoginUser();
    loginUser.value = res.data.data;
  }
  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser;
  }

  return { loginUser, fetchLoginUser, setLoginUser }
})
