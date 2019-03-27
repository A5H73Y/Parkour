$(function(){
    $("#navigation-placeholder").load("templates/nav.html");

    let contentsPlaceholder = $("#contents-placeholder");

    if (contentsPlaceholder) {
        let contents = "<ol>";
        $(".col-12 h2").each(function () {

            let heading = $(this);
            let title = heading.text();
            let link = "#" + heading.attr("id");

            contents += "<li><a href='" + link + "'>" + title + "</a></li>";
        });
        contents += "</ol>";

        contentsPlaceholder.html(contents);
    }
});