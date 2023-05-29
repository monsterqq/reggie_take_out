function loginApi(data) {
  return $axios({
    'url': '/employee/login',
    'method': 'post',
    data     //调用的this.loginForm=data,封装的用户名和密码
  })
}

function logoutApi(){
  return $axios({
    'url': '/employee/logout',
    'method': 'post',
  })
}
