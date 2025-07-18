<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Markdown 编辑器组件
  使用 Editor.md 实现的 Markdown 编辑器
  
  参数说明：
  - id: 编辑器容器的ID（必填）
  - name: 表单字段名（必填）
  - content: 初始内容（可选）
  - height: 编辑器高度，默认500px（可选）
  - width: 编辑器宽度，默认100%（可选）
  - saveHTMLToTextarea: 是否保存HTML到隐藏的textarea，默认true（可选）
  - imageUploadURL: 图片上传地址（可选）
  - placeholder: 占位文本（可选）
--%>

<%-- 编辑器容器 --%>
<div id="${param.id}">
    <c:if test="${not empty param.content}">
        <textarea style="display:none;" name="${param.name}">${param.content}</textarea>
    </c:if>
    <c:if test="${empty param.content}">
        <textarea style="display:none;" name="${param.name}"></textarea>
    </c:if>
</div>

<%-- 初始化脚本 --%>
<script>
$(document).ready(function() {
    var editor = editormd("${param.id}", {
        width: "${empty param.width ? '100%' : param.width}",
        height: "${empty param.height ? '500' : param.height}",
        path: "${pageContext.request.contextPath}/resources/lib/editor.md/lib/",
        saveHTMLToTextarea: ${empty param.saveHTMLToTextarea ? 'true' : param.saveHTMLToTextarea},
        placeholder: "${empty param.placeholder ? '请输入内容...' : param.placeholder}",
        <c:if test="${not empty param.imageUploadURL}">
        imageUpload: true,
        imageFormats: ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
        imageUploadURL: "${param.imageUploadURL}",
        </c:if>
        toolbarIcons: function() {
            return [
                "undo", "redo", "|",
                "bold", "del", "italic", "quote", "|",
                "h1", "h2", "h3", "h4", "h5", "h6", "|",
                "list-ul", "list-ol", "hr", "|",
                "link", "image", "code", "preformatted-text", "code-block", "table", "|",
                "watch", "preview", "fullscreen", "search"
            ];
        },
        onload: function() {
            // 编辑器加载完成后的回调
            console.log('Editor.md 加载完成');
        }
    });
    
    // 将编辑器实例保存到全局对象中，方便外部调用
    window.editor_${param.id} = editor;
});
</script>
