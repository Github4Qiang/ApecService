<%@page language="java" contentType="text/html; charset=UTF-8" %>

<html>
<body>
<h2>Hello World!</h2>

Spring-MVC 文件上传
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="Spring-MVC 上传文件"/>
</form>

Simditor 富文本文件上传
<form name="form2" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本文件上传"/>
</form>
</body>
</html>
