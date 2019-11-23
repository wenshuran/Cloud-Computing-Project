<template>
  <div id="main">
    <el-container style="height: 500px; border: 1px solid #eee;">
      <el-aside width="13%" style="background-color: rgb(238, 241, 246);">
        <div style="margin-top: 20%; font-size: 25px">Welcome, {{username}}</div>
        <el-input
          placeholder="Filter" @keypress.enter.native="search"
          v-model="filterText" style="margin-top: 10%">
        </el-input>

        <el-button autofocus=true @click="tableData=activeEvent" style="font-size: 23px; margin-left: 0; border-left: 0px; padding-left: 28px; padding-right: 28px">Active Events</el-button>
        <el-button @click="tableData=oldEvent" style="font-size: 23px; margin-left: 0; border-left: 0px; padding-left: 42px; padding-right: 42px">Old Events</el-button>
      </el-aside>

      <el-container>
        <el-header style="text-align: right; font-size: 18px">
          <i class="el-icon-refresh" @click="reload"></i>
          <el-button @click="shareEvent = true" v-if="share">Share</el-button>
          <el-button @click="addEvent = true">Add Event</el-button>
        </el-header>

        <el-main>
          <el-table :data="tableData" @selection-change="selectRow">
            <el-table-column
              type="selection"
              width="55">
            </el-table-column>
            <el-table-column prop="title" label="Event List">
            </el-table-column>
            <el-table-column prop="date" label="End Date">
            </el-table-column>
            <el-table-column prop="option" label="Option" fixed="right">
              <template slot-scope="scope">
                <el-button size="mini" @click="handleEdit(scope.row)">Edit</el-button>
                <el-button size="mini" type="danger" @click="handleDelete(scope.row)">Delete</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-main>
      </el-container>
    </el-container>
    <el-dialog
      title="Create Event"
      :visible="addEvent"
      width="30%"
      :before-close="handleClose">
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="Title">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="Organiser">
          <el-input v-model="form.organiser"></el-input>
        </el-form-item>
        <el-form-item label="Time start">
          <el-date-picker type="datetime" placeholder="select date" v-model="form.startTime" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item label="Time end">
          <el-date-picker type="datetime" placeholder="select date" v-model="form.endTime" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item label="Content">
          <el-input type="textarea" v-model="form.content"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">Create</el-button>
          <el-button @click="addEvent = false">Cancel</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <el-dialog
      title="Event"
      :visible="editEvent"
      width="30%"
      :before-close="handleClose">
      <el-form ref="form" :model="event" label-width="80px">
        <el-form-item label="Title">
          <el-input v-model="event.title"></el-input>
        </el-form-item>
        <el-form-item label="Organiser">
          <el-input v-model="event.organiser"></el-input>
        </el-form-item>
        <el-form-item label="Time start">
          <el-date-picker type="datetime" placeholder="select date" v-model="event.startTime" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item label="Time end">
          <el-date-picker type="datetime" placeholder="select date" v-model="event.endTime" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item label="Content">
          <el-input type="textarea" v-model="event.content"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onEditSubmit">Submit</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <el-dialog
      title="Share Event"
      :visible="shareEvent"
      width="20%"
      :before-close="handleClose">
      <el-input
        placeholder="username"
        v-model="shareTo"
        clearable>
      </el-input>
      <el-button @click="shareTablesToUser">Share</el-button>
    </el-dialog>
  </div>
</template>

<script>
    export default {
        name: "main",
        data(){
            return{
                filterText: '',
                editEvent: false,
                fullscreenLoading: false,
                addEvent: false,
                form:{
                    title: '',
                    organiser: '',
                    startTime: '',
                    endTime: '',
                    content: '',
                },
                event:{
                    title: '',
                    organiser: '',
                    startTime: '',
                    endTime: '',
                    content: '',
                },
                activeEvent: [],
                oldEvent: [],
                tableData: '',
                username: '',
                share: false,
                shareEvent: false,
                shareTo: '',
                shareTables: [],
            }
        },
        mounted:function(){
            this.getList();
        },
        computed(){
            return{

            }
        },
        methods: {
            shareTablesToUser(){
                this.$http({
                    method: 'post',
                    data: {
                        "username": this.shareTo,
                    },
                    url: 'http://localhost:8001/find'
                }).then(result => {
                    if (result.status === 200){
                        this.$http({
                            method: 'post',
                            data: {
                                "from": this.username,
                                "to": this.shareTo,
                                "tables": this.shareTables
                            },
                            url: 'http://localhost:8001/share'
                        }).then(result => {
                            this.shareTo = '';
                            this.shareEvent = false;
                            this.$message.success("Share success!");
                            window.location.reload();
                            //console.log(result);
                        }).catch(err => {
                                this.$message.error('Fail! ' + err.response);
                            }
                        );
                    }
                    //console.log(result);
                }).catch(err => {
                        this.$message.error('No such User!');
                    }
                );
            },
            selectRow(val){
                if (val.length > 0){
                    this.share = true;
                    this.shareTables = val;
                }
                else {
                    this.share = false;
                }
            },
            search(){
                var keyWord = this.filterText;
                var filterResult = [];
                var index;
                for (index in this.activeEvent) {
                    if (this.activeEvent[index].title.indexOf(keyWord) !== -1) {
                        //console.log(this.activeEvent[index]);
                        filterResult.push(this.activeEvent[index]);
                    }
                }
                for (index in this.oldEvent) {
                    if (this.oldEvent[index].title.indexOf(keyWord)!==-1){
                        //console.log(this.oldEvent[index]);
                        filterResult.push(this.oldEvent[index]);
                    }
                }
                //console.log(filterResult);
                this.tableData = filterResult;
            },
            judgeStatus: function(oldTime){
                var time = new Date();
                // let oldTime = row.row.date;
                if (oldTime.substr(0,4) < time.getFullYear()){
                    return "Old";
                }
                else if (oldTime.substr(0,4) > time.getFullYear()){
                    return "Active";
                }
                else if (oldTime.substr(5,2) < time.getMonth()+1){
                    return "Old";
                }
                else if (oldTime.substr(5,2) > time.getMonth()+1){
                    return "Active";
                }
                else if (oldTime.substr(8,2) < time.getDate()){
                    return "Old";
                }
                else if (oldTime.substr(8,2) >= time.getDate()){
                    return "Active";
                }
            },
            handleEdit(row){
                this.editEvent = true;
                this.$http({
                    method: 'post',
                    data: {
                        "user": this.username,
                        "title": row.title,
                        "date": row.date,
                    },
                    url: 'http://localhost:8001/getObject'
                }).then(result => {
                    //console.log(result.data);
                    this.event = result.data;
                }).catch(err => {
                        this.$message.error('Login Fail! ' + err.response.data.message);
                    }
                );
                //console.log(row);
            },
            handleDelete(row){
                this.$confirm('Are you sure to delete?')
                    .then(_ => {
                        this.$http({
                            method: 'delete',
                            data: {
                                "user": this.username,
                                "title": row.title,
                                "date": row.date,
                            },
                            url: 'http://localhost:8001/delete'
                        }).then(result => {
                            //console.log(result.data);
                            window.location.reload();
                        }).catch(err => {
                                this.$message.error('Login Fail! ' + err.response.data.message);
                            }
                        );
                        done();
                    })
                    .catch(_ => {});
            },
            reload(){
                window.location.reload();
            },
            getList(){
                this.username = sessionStorage.getItem("username");
                // this.username = "test";
                this.$http({
                    method: 'post',
                    data: {
                        "username": this.username,
                    },
                    // url: 'https://5c39574f-0591-4486-abbc-aafa89d4dbec.mock.pstmn.io/amazonS3/list'
                    url: 'http://localhost:8001/amazonS3/list'
                }).then(result => {
                    //this.tableData = result.data;
                    var resultData;
                        for (resultData in result.data){
                            if (this.judgeStatus(result.data[resultData].date) === "Active"){
                            this.activeEvent.push(result.data[resultData]);
                        }
                        else {
                            this.oldEvent.push(result.data[resultData]);
                        }
                        this.tableData = this.activeEvent;
                    }
                }).catch(err => {
                        this.$message.error('Connection error! ');
                    }
                );
            },
            handleClose(done) {
                this.$confirm('Are you sure to leave?')
                    .then(_ => {
                        this.addEvent = false;
                        this.editEvent = false;
                        this.shareEvent = false;
                        done();
                    })
                    .catch(_ => {});
            },
            onEditSubmit(){
                this.$http({
                    method: 'post',
                    data: {
                        "user": this.username,
                        "title": this.event.title,
                        "organiser": this.event.organiser,
                        "startTime": this.event.startTime,
                        "endTime": this.event.endTime,
                        "content": this.event.content
                    },
                    url: 'http://localhost:8001/amazonS3/editEvent'
                }).then(result => {
                    //console.log(result);
                    if (result.status === 200){
                        window.location.reload();
                    }
                }).catch(err => {
                        this.$message.error('Login Fail! ' + err.response.data.message);
                    }
                );
            },
            onSubmit(){
                this.fullscreenLoading = true;
                this.$http({
                    method: 'post',
                    data: {
                        "user": this.username,
                        "title": this.form.title,
                        "organiser": this.form.organiser,
                        "startTime": this.form.startTime,
                        "endTime": this.form.endTime,
                        "content": this.form.content
                    },
                    url: 'http://localhost:8001/amazonS3/addEvent'
                }).then(result => {
                    this.fullscreenLoading = false;
                    console.log(result);
                    if (result.status === 200){
                        window.location.reload();
                    }
                }).catch(err => {
                        this.fullscreenLoading = false;
                        this.$message.error('Login Fail! ' + err.response.data.message);
                    }
                );
            },
        }
    }
</script>

<style scoped>
  .el-header {
    background-color: #B3C0D1;
    color: white;
    line-height: 60px;
  }

  .el-aside {
    color: #333;
  }

</style>
