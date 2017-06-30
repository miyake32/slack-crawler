
///////////////////////////////////
// Message
///////////////////////////////////
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
				if (!isFirstLoad && $('#messages').scrollTop() == 0) {
					$('#messages').scrollTop(1);
				}
				messages.forEach(function(message) {
					$messageContainer = $('<div class="list-group">');
					$message = $('<a class="list-group-item">').prependTo(
							$messageContainer);
					$('<h5 class="list-group-item-heading">').html((message.userRealName ? message.user + ' - ' + message.userRealName : message.user) + '<small class="message-date" style="float: right;">' + formatTs(message.ts) + '</small>')
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
};


var searchMessage = function(keyword, channelIds) {
	$('#search-messages').children().remove();
	$.ajax('/slack-crawler/search', {
		'type' : 'GET',
		'dataType' : 'json',
		data : {
			'channel' : channelIds,
			'keyword' : keyword
		}
	}).done(
			function(messages) {
				messages.forEach(function(message) {
					$messageContainer = $('<div class="list-group">');
					$message = $('<a class="list-group-item">').prependTo(
							$messageContainer);
					$('<h5 class="list-group-item-heading">').html((message.userRealName ? message.user + ' - ' + message.userRealName : message.user) + '<small class="message-date" style="float: right;margin-top:2px;">' + formatTs(message.ts) + '</small><span class="label label-primary" style="float:right;margin-right:10px;">' + message.channelName + '</span>')
								.appendTo($message);
					$('<p class="list-group-item-text">').html(message.text).appendTo(
							$message);
					$('#search-messages').prepend($messageContainer);
				});
		}).fail(
				function(error) {
					console.log("searchMessage is failed");
					console.log(error);
				}
		);

}

var toggleViewAndSearch = function(search) {
	if (search) {
		$('#message-view').hide();
		$('#message-search').show();
		unbindChannelClick();
		bindSelectChannelsOnChannelClick();
		$('#search-input').focus();
	} else {
		$('#message-search').hide();
		$('#message-view').show();
		unbindChannelClick();
		bindGetMessageOnChanelClick();
	}
};


//////////////////////////////////
// Event binding fucntions
//////////////////////////////////
var bindGetMessageOnChanelClick = function () {
	$('#channels .active').removeClass('active');
	var channelId = localStorage.getItem('currentChannel');
	$('#channel-' + channelId).addClass('active');
	
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
};

var bindSelectChannelsOnChannelClick = function() {
	$('#channels').children().toArray().forEach(function(channel, index) {
		$(channel).on('click', function(event) {
			if ($(event.target).hasClass('active')) {
				$(event.target).removeClass('active');
			} else {
				$(event.target).addClass('active');
			}
		});
	});

};

var unbindChannelClick = function() {
	$('#channels').children().toArray().forEach(function(channel, index) {
		$(channel).off('click');
	});
};

//////////////////////////////////
// Utility functions
//////////////////////////////////
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


//////////////////////////////////
// executed after load
//////////////////////////////////
$(document).ready(function() {
	bindGetMessageOnChanelClick();
	
	$('#messages').on('scroll', function(event) {
		if ($('#messages').scrollTop() < 600) {
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
	
	// button
	$('#switch-to-search-button').on('click', function(){toggleViewAndSearch(true);});
	$('#close-search-button').on('click', function(){toggleViewAndSearch(false)});
	
	$('#search-form').on('submit', function(event) {
		var channelIds = "";
		$('#channels .active').each(function(index, elem) {
			if (index > 0) {
				channelIds = channelIds + " ";
			}
			channelIds = channelIds + elem.id.replace(/^channel-/, '');
		});
		searchMessage($('#search-input').val(), channelIds);
		return false;
	});
	
	// start crawling
	$.ajax('/slack-crawler/crawler/enable');
});
