jQuery(document).ready(function($) {
    //$('article').hide();
    $('#design-goals').show();
    $('.about-nav').click(function(e) {
        var articleId = $(this).data('article');
        $('article').hide();
        $('#' + articleId).show();
        $('html,body').animate({scrollTop:0},'slow');
    })
});
