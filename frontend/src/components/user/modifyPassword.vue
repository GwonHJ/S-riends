<template>
  <div class="grid grid-cols-6">

      <div class="col-start-2 col-span-4 ">
          <div class="grid grid-cols-6  mt-10">
              <div class="md:col-start-2 md:col-span-4 col-start-1 col-span-6 shadow-md border-solid border-2 border-yellow-500 rounded-md ml-2">
                  <form @submit.prevent="submitForm">
                      <div class="md:pt-5 md:pl-20 pt-5 pl-5">
                          <p class="text-xl font-bold">Password</p>
                          <input id="password" v-model="form.password" type="password" class="text-xl w-3/4 rounded-md border-2 border-yellow-400 mt-2"/>
                          <p class="w-3/4">
                              <span v-if="!isPasswordValid" class=" text-yellow-600">비밀번호는 영문,숫자,특수문자가 포함된 8자 이상으로 이루어져야합니다.</span>
                          </p>
                      </div>
                      <div class="md:pt-5 md:pl-20 pt-5 pl-5">
                          <p class="text-xl font-bold">비밀번호를 한 번 더 입력해주세요.</p>
                          <input id="passwordConfirm" v-model="confirmPassword" type="password" class="text-xl w-3/4 rounded-md border-2 border-yellow-400 mt-2"/>
                          <p class="w-3/4">
                              <span v-if="!isSame" class=" text-yellow-600">비밀번호가 일치하지 않습니다.</span>
                          </p>
                      </div>
                      <div class="flex justify-center p-2 mt-10">
                        <!-- <button class="border-solid border-2 border-yellow-500 rounded-md hover:bg-yellow-400 w-20 h-10">Log In</button> -->
                        <button type="submit"  class="border-solid border-2 border-yellow-500 rounded-md hover:bg-yellow-400 w-20 h-10">
                            확인
                        </button>
                      </div>
                  </form>

              </div>
          </div>
      </div>
  </div>
</template>

<script>
import Swal from 'sweetalert2'
// import axios from 'axios'
// const SERVER_URL = process.env.VUE_APP_SERVER_URL
import { validatePassword } from '@/utils/passwordValidation.js';
import { modifyPassword } from '@/api/auth.js'
export default {
 data(){
   return{
        confirmPassword:'',
        form:{
            password:'',
        }  
   }
 },
 methods:{
   submitForm(){
    //  axios({
    //   method:'put',
    //   url: `${SERVER_URL}/users/password`,
    //   headers: this.getToken,
    //   data: this.form
    //  })
    modifyPassword(this.form)
     .then((res)=>{
       console.log(res)
       Swal.fire('비밀번호가 변경되었습니다.')
       this.$router.push('/main')
     }).catch((err)=>{
       console.log(err)
     })
   }
 },
 computed: {
    isPasswordValid(){
            return validatePassword(this.form.password);
    },
    isSame(){
        if(this.form.password != this.confirmPassword){
            return false;
        }
        return true;
    },
    getToken(){
        const token = localStorage.getItem('token')
        const config = {
            Authorization: `Bearer ${token}`
        }
        return config
    },
}
}
</script>

<style>

</style>