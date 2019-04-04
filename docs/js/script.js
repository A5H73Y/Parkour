$(function(){
    $("#navigation-placeholder").load("templates/nav.html");

    let contentsPlaceholder = $("#contents-placeholder");

    if (contentsPlaceholder) {
        let contents = "<ul>";
        $(".col-12 h2").each(function () {
            let heading = $(this);
            let id = heading.attr("id");
            if (id) {
                let title = heading.text();
                let link = "#" + id;

                contents += "<li><a href='" + link + "'>" + title + "</a></li>";
            }
        });
        contents += "</ul>";
        contentsPlaceholder.html(contents);
    }

    $('img').on("click",function(){
        var url = $(this).attr("src");
        window.open(url, '_blank');
    });
});