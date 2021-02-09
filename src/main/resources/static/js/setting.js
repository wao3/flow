$(function () {
    let uploadForm = $("#uploadForm");
    uploadForm.submit(upload);
    function upload() {
        $.ajax({
            url: "http://upload-z2.qiniup.com",
            method: "post",
            processData: false,
            contentType: false,
            data: new FormData(uploadForm[0]),
            success: (data) => {
                if (data != null && data.code === 0) {
                    // 更新头像访问路径
                    let fileName = $("input[name='key']").val();
                    updateHeaderUrl(fileName);
                } else {
                    alert("上传失败")
                }
            }
        })
        return false;
    }
    function updateHeaderUrl(fileName) {
        $.post(CONTEXT_PATH + "/user/header/url", { fileName }, (data) => {
            data = JSON.parse(data);
            if (data.code === 0) {
                window.location.reload();
            } else {
                alert(data.msg);
            }
        })
    }
});