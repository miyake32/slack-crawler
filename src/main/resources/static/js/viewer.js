// /////////////////////////////////
// Message related functions
// /////////////////////////////////
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
  }).done(function(messages) {
    var isFirstLoad = $('#messages').children().length == 0;
    if (!isFirstLoad && $('#messages').scrollTop() == 0) {
      $('#messages').scrollTop(1);
    }
    reflectMessages(messages, '#messages');
    if (isFirstLoad) {
      $('#messages').animate({
        scrollTop : $('#messages')[0].scrollHeight
      }, 0);
    }
    getMessageIsRunning = false;
  }).fail(function(error) {
    console.log("getMessage is failed");
    console.log(error);
    getMessageIsRunning = false;
  });
};

var searchMessage = function() {
  var keyword = $('#search-input').val();
  var channelIds = "";
  $('#channels .active').each(function(index, elem) {
    if (index > 0) {
      channelIds = channelIds + " ";
    }
    channelIds = channelIds + elem.id.replace(/^channel-/, '');
  });

  $('#search-messages').children().remove();
  $.ajax('/slack-crawler/search', {
    'type' : 'GET',
    'dataType' : 'json',
    data : {
      'channel' : channelIds,
      'keyword' : keyword
    }
  }).done(function(messages) {
    reflectMessages(messages, '#search-messages');
  }).fail(function(error) {
    console.log("searchMessage is failed");
    console.log(error);
  });
};

var reflectMessages = function(messages, messageArea) {
  messages.forEach(function(message) {
    $messageContainer = $('<div class="list-group">');
    $message = $('<a class="list-group-item">').prependTo($messageContainer);

    var messageHeaderHtml = '';
    if (message.user) {
      messageHeaderHtml = messageHeaderHtml
          + (message.userRealName ? message.user + ' - ' + message.userRealName : message.user);
    }
    messageHeaderHtml = messageHeaderHtml + '<small class="message-date">' + formatTs(message.ts) + '</small>';
    if (message.channelName) {
      messageHeaderHtml = messageHeaderHtml + '<span class="label label-primary search-result-channel">'
          + message.channelName + '</span>';
    }

    $('<h5 class="list-group-item-heading">').html(messageHeaderHtml).appendTo($message);
    $('<p class="list-group-item-text">').html(message.text).appendTo($message);
    $(messageArea).prepend($messageContainer);
  });
};

// ////////////////////////////////
// User related functions
// ////////////////////////////////
// initialized in init process
var users;

var suggestUsers = function() {
  var keyword = getKeyword();
  $('#user-suggest-list').children().remove();
  if (!keyword) {
    return;
  }
  var valPrefix = '';
  var valPrefixMatch = $('#search-input').val().match(/^.*\s(?=[^\s]+$)/);
  if (valPrefixMatch) {
    valPrefix = valPrefixMatch[0];
  }

  // don't show candidates if 2 users are already specified
  // because of limitation of sql written in server-side logic
  var valMatch = valPrefix.match(/[^\s]+/g);
  var userCnt = 0;
  if (valMatch) {
    valMatch.forEach(function(val) {
      if (users.contains(val)) {
        userCnt++;
      }
    });
  }
  if (userCnt >= 2) {
    return;
  }

  var userCand = users.prefixSearch(keyword);
  if (userCand.length === 1 && valPrefix + userCand[0] === $('#search-input').val()) {
    return;
  }
  if (userCand.length > 5) {
    userCand = userCand.slice(0, 5);
  }
  var html;
  if (userCand.length === 1) {
    html = '<option value="' + valPrefix + userCand[0] + ' ">';
  } else {
    html = '<option value="' + valPrefix + userCand.join('" >' + '<option value="' + valPrefix) + ' ">';
  }
  $('#user-suggest-list').html(html);
};

var lastWordInInputCache;
var getKeyword = function() {
  var lastWordInInput;
  var lastWordInInputMatch = $('#search-input').val().match(/[^\s]+$/);
  if (lastWordInInputMatch) {
    lastWordInInput = lastWordInInputMatch[0];
  } else {
    lastWordInInputCache = null;
    return null;
  }
  if (lastWordInInput === lastWordInInputCache) {
    return null;
  } else {
    lastWordInInputCache = lastWordInInput;
    return lastWordInInput;
  }
};

// ////////////////////////////////
// Switch functions
// ////////////////////////////////
var toggleViewAndSearch = function(search) {
  if (search) {
    $('#message-view').hide();
    $('#message-search').show();
    unbindChannelClick();
    bindSelectChannelsOnChannelClick();
    $('#search-input').focus();
  } else {
    $('#message-search').hide();
    $('#search-messages').children().remove();
    $('#search-input').val('');
    $('#message-view').show();
    unbindChannelClick();
    bindGetMessageOnChanelClick();
  }
};

// ////////////////////////////////
// Event binding functions
// ////////////////////////////////
var bindGetMessageOnChanelClick = function() {
  $('#channels .active').removeClass('active');
  var channelId = localStorage.getItem('currentChannel');
  $('#channel-' + channelId).addClass('active');

  $('#channels').children().toArray().forEach(function(channel, index) {
    $(channel).on('click', function(event) {
      var channel;
      if ($(event.target).hasClass('channel')) {
        channel = $(event.target);
      } else {
        channel = $(event.target).parents('.channel');
      }

      $('#messages').children().remove();
      $('#channels .active').removeClass('active');
      $('#active-channel-name').text(channel.text());

      channel.addClass('active');

      var channelId = channel.prop('id').replace(/^channel-/, '');
      getMessage(channelId, null);
      localStorage.setItem('currentChannel', channelId);
    });
  });
};

var bindSelectChannelsOnChannelClick = function() {
  $('#channels').children().toArray().forEach(function(channel, index) {
    $(channel).on('click', function(event) {
      var channel;
      if ($(event.target).hasClass('channel')) {
        channel = $(event.target);
      } else {
        channel = $(event.target).parents('.channel');
      }
      if (channel.hasClass('active')) {
        channel.removeClass('active');
      } else {
        channel.addClass('active');
      }
      searchMessage();
    });
  });

};

var unbindChannelClick = function() {
  $('#channels').children().toArray().forEach(function(channel, index) {
    $(channel).off('click');
  });
};

var bindSuggestUsersOnInput = function() {
  $('#search-input').on('input', suggestUsers);
};

// ////////////////////////////////
// Utility functions
// ////////////////////////////////
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
      + toDoubleDigits(date.getHours()) + ':' + toDoubleDigits(date.getMinutes()) + ':'
      + toDoubleDigits(date.getSeconds());
};

var BinaryTree = function(value) {
  this.value = value;
}
BinaryTree.prototype.add = function(value) {
  var node = new BinaryTree(value);
  if (this.value > node.value) {
    if (this.lower) {
      this.lower.add(value);
    } else {
      this.lower = node;
    }
  } else {
    if (this.higher) {
      this.higher.add(value);
    } else {
      this.higher = node;
    }
  }
};
BinaryTree.prototype.prefixSearch = function(keyword) {
  var ret = [];
  if (!this.value) {
    return ret;
  }
  if (this.value.startsWith(keyword)) {
    ret.push(this.value);
    if (this.lower) {
      ret = ret.concat(this.lower.prefixSearch(keyword));
    }
    if (this.higher) {
      ret = ret.concat(this.higher.prefixSearch(keyword));
    }
  } else {
    if (this.value > keyword) {
      if (this.lower) {
        ret = ret.concat(this.lower.prefixSearch(keyword));
      }
    } else {
      if (this.higher) {
        ret = ret.concat(this.higher.prefixSearch(keyword));
      }
    }
  }
  return ret;
};
BinaryTree.prototype.contains = function(keyword) {
  if (this.value === keyword) {
    return true;
  }
  if (this.value > keyword) {
    if (this.lower) {
      return this.lower.contains(keyword);
    } else {
      return false;
    }
  } else {
    if (this.higher) {
      return this.higher.contains(keyword);
    } else {
      return false;
    }
  }
};

// ////////////////////////////////
// init process
// ////////////////////////////////
$(document).ready(function() {
  bindGetMessageOnChanelClick();

  $('#messages').on('scroll', function(event) {
    if ($('#messages').scrollTop() < 600) {
      var ts = $('#messages').children().first().find('.message-body').attr('data-ts');
      var channelId = localStorage.getItem('currentChannel');
      getMessage(channelId, ts);
    }
  });

  // button
  $('#switch-to-search-button').on('click', function() {
    toggleViewAndSearch(true);
  });
  $('#close-search-button').on('click', function() {
    toggleViewAndSearch(false)
  });

  $('#search-form').on('submit', function(event) {
    searchMessage();
    return false;
  });

  // start crawling
  $.ajax('/slack-crawler/crawler/enable');

  // select channel
  if (localStorage.getItem('currentChannel')) {
    $('#channel-' + localStorage.getItem('currentChannel')).click();
  } else {
    $('#channels').children()[0].click();
  }

  // retrieve users
  $.ajax('/slack-crawler/user/all', {
    'type' : 'GET',
    'dataType' : 'json'
  }).then(function(fetchedUsers) {
    if (!fetchedUsers) {
      return;
    }
    for (var i = 0; i < fetchedUsers.length; i++) {
      if (i === 0) {
        users = new BinaryTree(fetchedUsers[i].name);
      } else {
        users.add(fetchedUsers[i].name);
      }
    }
    bindSuggestUsersOnInput();
  });
});
