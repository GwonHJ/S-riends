<template>
<div class="flex antialiased text-gray-800">
    <div class="flex flex-row h-full w-full  overflow-x-hidden">
      <div class="flex flex-col flex-auto  h-full p-6">
        <div
          class="flex flex-col flex-auto flex-shrink-0 rounded-2xl  bg-gray-100 h-full p-4"
        >
            <div class="text-2xl">{{oppenentName}}님과의 대화</div>

          <div id="chatdiv" class="flex flex-col h-120 overflow-x-auto mb-4">
            <div id="chatdiv" class="flex flex-col h-screen max-h-96">
              
              <div class="grid grid-cols-12 gap-y-2">

                <div  v-for="item in recvList" :key="item.id" class="col-start-1 col-end-12 p-3 rounded-lg">
                  <div v-if="item.sender==oppenentId" class="flex flex-row items-center">
                    <div @click="clickUser" class="cursor-pointer">
                    <img @error="imgError"
                      :src="partnerPicture"
                      class="flex items-center justify-center h-10 w-10 rounded-full bg-yellow-200 flex-shrink-0"
                    />
                      </div>
                    
                    <div
                      class="relative ml-3 text-sm bg-white py-2 px-4 shadow rounded-xl"
                    >
                      <div>{{item.message}}</div>
                    </div>
                    <!-- <div>{{item.time}}</div> -->
                  </div>
                  <div v-if="item.sender==Myid" class="flex items-end justify-start flex-row-reverse">
                    <img
                      :src="myPicture" @error="imgError"
                      class="flex items-center justify-center h-10 w-10 rounded-full bg-yellow-200 flex-shrink-0"
                    />
                    <div
                      class="relative mr-3 text-sm bg-yellow-300 py-2 px-4 shadow rounded-xl"
                    >
                      <div>{{item.message}}</div>
                      <!-- <div>{{item.time}}</div> -->
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div
            class="flex flex-row items-center h-16 rounded-xl bg-white w-full px-4"
          >
            <div>
            </div>
            <div class="flex-grow ml-4">
              <div class="relative w-full">
                <input
                  type="text" v-model="message"
                  class="flex w-full border rounded-xl focus:outline-none focus:border-indigo-300 pl-4 h-10"
                />
              </div>
            </div>
            <div class="ml-4">
              <button @keyup.enter="sendMessage" @click="sendMessage"
                class="flex items-center justify-center bg-yellow-500 hover:bg-yellow-600 rounded-xl text-white px-4 py-1 flex-shrink-0"
              >
                <span>Send</span>
                <span class="ml-2">
                  <svg
                    class="w-4 h-4 transform rotate-45 -mt-px"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"
                    ></path>
                  </svg>
                </span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import $ from 'jquery'


import img from '@/assets/profile.png'
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'
import store from '@/store/index.js'
import axios from 'axios'
const SERVER_URL = process.env.VUE_APP_SERVER_URL


export default {

    data(){
        return{
            reconnect:0,
            message:'',
            roomId:store.state.roomId,
            oppenentId:store.state.partnerId,
            oppenentName:store.state.partnerName,
            userName:store.state.userName,
            recvList:[],
            partnerPicture:store.state.partnerPicture,
            Myid:store.state.userId,
            myPicture:store.state.myPicture,
            // items:[
            //     {
            //         name:'testA',
            //         message:'HiHi',
            //         userId:1
            //     },
            //     {
            //         name:'testB',
            //         message:'Hello',
            //         userId:3
            //     }
            // ]
        }
    },
    mounted(){
      window.addEventListener('keyup', event => {
      if (event.keyCode === 13) { 
        this.sendMessage()
      }
    })
    },
    watch: {
        messages() {
            // 화면에 추가된 후 동작하도록
            this.$nextTick(() => {
                let messages = this.$refs.messages;

                messages.scrollTo({ top: messages.scrollHeight, behavior: 'smooth' });
            });
        },
    },
    created() {
     $("#chatdiv").scrollTop($(document).height())
        // App.vue가 생성되면 소켓 연결을 시도합니다.
        // this.findRoom();
        const token = store.state.accessToken
        
        // this.connect()
        
        this.connect();
        const path = store.state.chatPath
        if(path=='list'){
        // 리스트에서 불러올 때 
          const roomId = store.state.roomId
          axios({
            method: 'get',
            url: `${SERVER_URL}/chat/room/by-list/${roomId}`,
            headers: {
                'Authorization': `Bearer ${token}`
            }
          })
          .then((res)=>{
            this.recvList = res.data
          }).catch((err)=>{
            console.log(err)
          })
        }else{
          const partnerId = store.state.chatOppenent
          axios({
            method: 'get',
            url: `${SERVER_URL}/chat/room/by-user/${partnerId}`,
            headers: {
                'Authorization': `Bearer ${token}`
            }
          })
          .then((res)=>{
            console.log(res.data)
            this.recvList = res.data.list
            this.roomId = res.data.roomId
            this.$store.commit('setRoomId',res.data.roomId)
          }).catch((err)=>{
            console.log(err)
          })
        
        }

    },

    methods: {
      clickUser:function(){
        this.$store.commit('setTempUserId',this.oppenentId)
        this.$router.push('/user')
      },
      imgError:function(e){
            e.target.src = img
        },
        sendMessage () {
            this.send()
            //  위의 sendMessage는 유효성검사
            // 조건에 충족하면 아래 send()이벤트 실행
        },    

        send() {
        console.log("Send message:" + this.message);
        // ws = this.stompClient
        if (this.stompClient && this.stompClient.connected) {
          let now = new Date();
          const utc = 
            now.getTime() + 
            (now.getTimezoneOffset() * 60 * 1000);

          const KR_TIME_DIFF = 9 * 60 * 60 * 1000;
          const today = 
                new Date(utc + (KR_TIME_DIFF));

            const msg = { 
            // userName: this.userName,
            roomId: this.roomId,
            message: this.message,
            sender: store.state.userId,
            time:today
            };
            if(this.message!=''){
            this.stompClient.send(`/pub/chat/message`, JSON.stringify(msg), {});
            }
        }else{
          console.log("not connect!")
        }
        this.message=''
        },    

        connect() {
        let socket = new SockJS(`${SERVER_URL}/ws-stomp`);
        const roomId = this.roomId
        this.stompClient = Stomp.over(socket);
        console.log(`소켓 연결을 시도합니다. 서버 주소:'https://k5d106.p.ssafy.io:/api/ws-stomp'`)
        this.stompClient.connect(
            {},
            frame => {
            // 소켓 연결 성공
            this.connected = true;
            console.log('소켓 연결 성공', frame);
            // 서버의 메시지 전송 endpoint를 구독합니다.
            // 이런형태를 pub sub 구조라고 합니다.
            console.log(roomId)
                this.stompClient.subscribe('/sub/chat/room/'+ roomId, res => {
                    console.log('res=>'+res)
                    console.log('subscribe 로 받은 메시지 입니다.', res.body);
                    // 받은 데이터를 json으로 파싱하고 리스트에 넣어줍니다.
                    this.recvList.push(JSON.parse(res.body))
                });
            },
            error => {
            // 소켓 연결 실패
            console.log('소켓 연결 실패', error);
            this.connected = false;
            }
        );        
        }
    },
    computed:{
    }


}



</script>

<style>
</style>