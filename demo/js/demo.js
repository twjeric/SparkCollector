var myInterval;

function getUrl(start) {
	let base_url = $('#base_url').val();
	let port = $('#port').val();
	let url = base_url + port + '/Crawler?start=' + start;
	if (start == 1) url = url + '&url=' + $('#q_url_'+port).val();
	return url;
}

function update() {
  submit(2);
}

function send(port) {
  $('#port').val(port);
  switch(port) {
    case 8501: $('#result_title').html('URL SparkCollector Result:'); break;
    case 8502: $('#result_title').html('Website SparkCollector Result:'); break;
    case 8503: $('#result_title').html('Keyword SparkCollector Result:'); break;
    default: $('#result_title').html('Result:');
  }
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
  let ol = $('#list');
  ol.empty();
  $.each(data, function(idx, item){
    ol.append('<li class="list-group-item"><span class="badge badge-secondary">' + item['_1'] + '</span>' + item['_2'] +'</li>');
  });
}
