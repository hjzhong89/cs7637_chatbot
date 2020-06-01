
jQuery(document).ready(function($) {
    initialize();

    $('#chat-form').submit(function(e) {
        e.preventDefault();
        $('.chat-window-message').hide();

        $('#typing').show();

        var message = getChatMessage($('#text').val(), 'user');
        $('#conversation').append(message.clone());

        var data = {};
        data['text'] = $('#text').val();
        data['user'] = $('#user').val();

        $('#text').val('');

        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "api/chat",
            data: JSON.stringify(data),
            dataType: 'json',
            success: function(data) {
                console.log(data);
                var responses = data.responses;
                if (!responses) {
                    return;
                }

                for (var i = 0; i < responses.length; i++) {
                    var message = getChatMessage(responses[i], 'agent');
                    $('#typing').hide();
                    $('#conversation').append(message.clone());
                    updateScroll();
                }

            },
            error: function(error) {
                $('#typing').hide();
                console.log(error);
                getChatMessage('An error occurred while processing the message', 'agent');
            }
        });
    });
});

function initialize() {
    updateScroll();
}

function updateScroll() {
    var chatWindow = document.getElementById('chat-window');
        chatWindow.scrollTop = chatWindow.scrollHeight;
}

function getChatMessage(text, speaker) {
    var chatMessage = $('<p>').addClass('chat-message');


    var speakerClass;
    var speakerName;

    if (speaker == 'user') {
        speakerClass = 'chat-message-user';
        speakerName = 'User';
    } else {
        speakerClass = 'chat-message-ai';
        speakerName = 'Agent';
    }

    var chatMessageSpeaker = $('<span>').addClass(speakerClass).text(speakerName + ": ");
    var chatMessageText = $('<span>').addClass('chat-message-text').html(text);
    chatMessage.append(chatMessageSpeaker.clone());
    chatMessage.append(chatMessageText.clone());
    return chatMessage;
}