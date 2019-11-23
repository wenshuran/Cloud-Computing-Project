<template>
<div id="signUp">
  <div style="display: flex;justify-content: center;margin-top: 150px">
    <el-card style="width: 30%" >
      <div slot="header" class="clearfix">
        <span>Sign Up</span>
      </div>
      <el-form ref="form" :model="signUpInfo" label-width="125px">
        <el-form-item label="username">
          <el-input v-model="signUpInfo.username" ref="username" @keyup.enter.native="signUp"></el-input>
        </el-form-item>
        <el-form-item label="password">
          <el-input v-model="signUpInfo.password" @keyup.enter.native="signUp"></el-input>
        </el-form-item>
        <el-form-item label="confirm password">
          <el-input v-model="confirmPassword" @keyup.enter.native="signUp"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="signUp" v-loading.fullscreen.lock="fullscreenLoading">Sign Up</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</div>
</template>

<script>
    export default {
        name: "SignUp",
        data() {
            return {
                signUpInfo: { username: '', email: '', password: '' },
                fullscreenLoading: false,
                confirmPassword: '',
            }
        },
        methods: {
            signUp() {
                if (this.signUpInfo.username === ''){
                    this.$message({
                        message: 'Please enter username',
                        type: 'warning',
                    });
                }
                else if (this.signUpInfo.password === ''){
                    this.$message({
                        message: 'Please enter password',
                        type: 'warning',
                    });
                }
                else if (this.signUpInfo.password !== this.confirmPassword){
                    this.$message({
                        message: "Two passwords don't match",
                        type: 'warning',
                    });
                }
                else{
                    this.fullscreenLoading = true;
                    this.$http({
                        method: 'post',
                        url: 'http://localhost:8001/signUp',
                        data: {
                            "username": this.signUpInfo.username,
                            "password": this.signUpInfo.password
                        }
                    }
                    ).then(result => {
                        console.log(result);
                        this.fullscreenLoading = false;
                        if (result.status === 200){
                            this.$message.success("Sign Up Success!")
                            this.$router.push('/');
                        }
                        else {
                            this.fullscreenLoading = false;
                            this.$message.error(result.data.message);
                        }
                    }).catch(err => {
                            this.fullscreenLoading = false;
                            this.$message.error("Connection Error!");
                            this.signUpInfo.password = '';
                            this.confirmPassword = '';
                    }
                    );
                }
            },
        }
    }
</script>

<style scoped>

</style>
