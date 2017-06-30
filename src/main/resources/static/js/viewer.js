/**
 * 
 */
var getMessageIsRunning = false;
var getMessage = function(channelId, currentMinTs) {
	if (getMessageIsRunning) {
		return;
	}
	getMessageIsRunning = true;
	$.ajax('/slack-crawler/getMessages', {
		'type' : 'GET',
		'dataType' : 'json',
		data : {
			'channelId' : channelId,
			'currentMinTs' : currentMinTs
		}
	}).done(
			function(messages) {
				var isFirstLoad = $('#messages').children().length == 0;
				messages.forEach(function(message) {
					$messageContainer = $('<div class="list-group">');
					$message = $('<a class="list-group-item">').prependTo(
							$messageContainer);
					$('<h5 class="list-group-item-heading">').html((message.userRealName ? message.user + ' - ' + message.userRealName : message.user) + '<span class="message-date" style="float: right;">' + formatTs(message.ts) + '</span>')
								.appendTo($message);
					$('<p class="list-group-item-text">').html(message.text).appendTo(
							$message);
					$('#messages').prepend($messageContainer);
				});
				if (isFirstLoad) {
					$('#messages').animate({ scrollTop: $('#messages')[0].scrollHeight }, 0);
				}
				getMessageIsRunning = false;
		}).fail(
				function(error) {
					console.log("getMessage is failed");
					console.log(error);
					getMessageIsRunning = false;
				}
		);
}

var toDoubleDigits = function(num) {
	  num += "";
	  if (num.length === 1) {
	    num = "0" + num;
	  }
	 return num;     
	};

var formatTs = function(ts) {
	var date = new Date(Number(ts.replace(/\..*/, '')) * 1000);
	return date.getFullYear() + '/' + toDoubleDigits(date.getMonth() + 1) + '/' + toDoubleDigits(date.getDate()) + ' '
		+ toDoubleDigits(date.getHours()) + ':' + toDoubleDigits(date.getMinutes()) + ':' + toDoubleDigits(date.getSeconds());
}

$(document).ready(function() {
	// load messages in the channel when clicked
	$('#channels').children().toArray().forEach(function(channel, index) {
		$(channel).on('click', function(event) {
			$('#messages').children().remove();
			$('#channels .active').removeClass('active');
			$('#active-channel-name').text($(channel).text());
			$(event.target).addClass('active');
			var target = $(event.target);
			while (!/channel-[a-zA-Z0-9]+/.test(target.prop('id'))) {
				target = target.parent();
			}
			var channelId = target.prop('id').replace(/^channel-/, '');
			getMessage(channelId, null);
			localStorage.setItem('currentChannel', channelId);
		});
	});
	
	$('#messages').on('scroll', function(event) {
		if ($('#messages').scrollTop() < 300) {
			var ts = $('#messages').children().first().find('.message-body').attr('data-ts');
			var channelId = localStorage.getItem('currentChannel');
			getMessage(channelId, ts);
		}
	});
	
	// select channel
	if (localStorage.getItem('currentChannel')) {
		$('#channel-' + localStorage.getItem('currentChannel')).click();
	} else {
		$('#channels').children()[0].click();
	}
	
	// start crawling
	$.ajax('/slack-crawler/crawler/enable');
});
