import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from "vuex-persistedstate"

Vue.use(Vuex)

export default new Vuex.Store({
  plugins:[createPersistedState()],
  state: {
    accessToken: '',
    userId: '',
    teamId: '',
    boardId: '',
    commentId:'',
    selectCity: '',
    selectSportCategory: '',
  },
  mutations: {
    setAccessToken(state,accessToken){
        state.accessToken = accessToken
    },
    setUserId(state, userId){
        state.userId = userId
    },
    setTeamId(state, teamId){
      state.teamId = teamId
    },
    setBoardId(state,boardId){
      state.boardId = boardId
    },
    setCommentId(state,commentId){
      state.commentId = commentId
    },
    setCity(state,selectCity){
      state.selectCity = selectCity
    },
    setSportCategory(state,selectSportCategory){
      state.selectSportCategory = selectSportCategory
    }
  }
})