/**
 * 
 */
var getMessage = function(channelId, currentMinTs) {
	$.ajax('getMessages', {
		'type' : 'GET',
		'dataType' : 'json',
		data : {
			'channelId' : channelId,
			'currentMinTs' : currentMinTs
		}
	}).done(
			function(messages) {
				messages.forEach(function(message) {
					$messageContainer = $('<div class="list-group">');
					$message = $('<a class="list-group-item">').prependTo(
							$messageContainer);
					if (message.user) {
						$('<h5 class="list-group-item-heading">').text(new Date(Number(message.ts.replace(/\..*/, '')) * 1000) + ' ' + (message.user.realName ? message.user.name + ' - ' + message.user.realName : message.user.name))
								.appendTo($message);
					}
					$('<p class="list-group-item-text">').html(toHtml(message.text, message.referencedUsers)).appendTo(
							$message);
					$('#messages').prepend($messageContainer);
				});
			});
}

var toHtml = function(messageText, referencedUsers) {
	var matches = messageText.match(/<@([A-Za-z0-9]+)(\|.*)?>/g);
	var users = {};
	if (referencedUsers) {
		for ( var i in referencedUsers) {
			users[referencedUsers[i].id] = referencedUsers[i];
		}
	}
	for (var i in matches) {
		var user = users[matches[i].replace(/<@([A-Za-z0-9]+)(\|.*)?>/, '$1')];
		console.log(matches[i].replace(/<@([A-Za-z0-9]+)(\|.*)?>/, '$1'));
		messageText = messageText.replace(matches[i], '<a>@' + user.name + '</a>');
	}
	return messageText.replace(/\r?\n/g, '<br/>');
}

$(document).ready(function() {
	// bind event on channels
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
	
	// select channel
	if (localStorage.getItem('currentChannel')) {
		$('#channel-' + localStorage.getItem('currentChannel')).click();
	} else {
		$('#channels').children()[0].click();
	}
	
	// start crawling
	$.ajax('crawler/enable');
});
