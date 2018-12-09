map = new BMap.Map("allmap");
map.centerAndZoom(new BMap.Point(116.3978,39.9033), 12);
map.enableScrollWheelZoom(true);
var opts = {
  width : 250,     // 信息窗口宽度
  height: 80,     // 信息窗口高度
  title : "Info. Window" , // 信息窗口标题
  enableMessage:true//设置允许信息窗发送短息
};

var myInterval;

function getUrl(start) {
	let base_url = $('#base_url').val();
	let url = base_url + 'Poi?start=' + start;
	if (start == 1) url = url + '&lon=' + $('#longitude').val() + '&lat=' + $('#latitude').val();
	return url;
}

function update() {
  submit(2);
}

function send() {
  map.clearOverlays();
  submit(1);
  myInterval = window.setInterval(update, 3000);
}

function submit(start) {
  if (start==0) window.clearInterval(myInterval);
  let url = getUrl(start);
  console.log(url);

  let xhr = new XMLHttpRequest();
  xhr.responseType = 'json';
  xhr.onreadystatechange = function () {
    if (xhr.readyState === 4) {
      console.log(xhr.response);
      if (start==2) display(xhr.response);
    }
  }
  xhr.open('get', url, true);
  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
  xhr.send();
}

function display(data) {
  map.clearOverlays();
  $.each(data, function(idx, item){
    let data_info = item['_2'].split(' ');
    let marker = new BMap.Marker(new BMap.Point(data_info[1],data_info[2]));
    let content = 'Rating: ' + item['_1'] + '<br>Location: ' + data_info[0];
    map.addOverlay(marker);
    addClickHandler(content,marker);
  });
}

function addClickHandler(content,marker){
  marker.addEventListener("click",function(e){
    openInfo(content,e)}
  );
}

function openInfo(content,e){
  var p = e.target;
  var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
  var infoWindow = new BMap.InfoWindow(content,opts);  // 创建信息窗口对象 
  map.openInfoWindow(infoWindow,point); //开启信息窗口
}
