/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function() {
    function updateMax() {
        
        switch(parseInt($("#direcao").val())) {
            case 1:
                $("#slice").attr('max', $("#y").val());
                break;
            
            case 2:
                $("#slice").attr('max', $("#z").val());
                break;
                
            case 3:
                $("#slice").attr('max', $("#x").val());
                break;
        }
        
        //console.log(parseInt($("#slice").val()), parseInt($("#slice").attr('max')));
        
        if (parseInt($("#slice").val()) > parseInt($("#slice").attr('max')))
            $("#slice").val(parseInt($("#slice").attr('max')));
    }
    $("#direcao").change(updateMax);
    $("#x").change(updateMax);
    $("#y").change(updateMax);
    $("#z").change(updateMax);
    
    $("#frm").submit(function(event) {
        event.stopPropagation();
        event.preventDefault();
        
        var data = new FormData($("form")[0]);
        $('.progress').css({
            width: 0
        }).removeClass('hide');
        $.ajax({
            xhr: function () {
                var xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener("progress", function (evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = evt.loaded / evt.total;
                        console.log(percentComplete);
                        $('.progress').css({
                            width: percentComplete * 100 + '%'
                        });
                        if (percentComplete === 1) {
                            $('.progress').addClass('hide');
                        }
                    }
                }, false);
                xhr.addEventListener("progress", function (evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = evt.loaded / evt.total;
                        console.log(percentComplete);
                        $('.progress').css({
                            width: percentComplete * 100 + '%'
                        });
                    }
                }, false);
                return xhr;
            },
            url: 'FileReceiverServlet',
            type: 'POST',
            data: data,
            cache: true,
            //dataType: 'binary',
            processData: false, // Don't process the files
            contentType: false,
//            beforeSend: function(x) {
//                x.overrideMimeType("text/plain; charset=x-user-defined");
//            }
        }).done(function(resposta, status, jqxhr) {
            encodedimage = resposta;
            
            //console.log('success:', encodedimage, status, jqxhr);
                                  
            $("#ResponseImg").attr('src', "data:image/jpeg;base64,"+encodedimage);
            //URL.revokeObjectURL( imgSrc );
        }).fail(function(jqxhr, status, erro) {
            console.log('erro:', status, erro, jqxhr);
        }).complete(function(jqxhr, status) {
            console.log('complete:', status, jqxhr); 
        });
    });
});