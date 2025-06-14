<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${assunto}</title>
</head>
<body>
<h1>Olá ${nome},</h1>

<p>${mensagem}</p>

<#if link??>
    <p>
        <a href="${link}">Clique aqui</a> para acessar.
    </p>
</#if>

<footer>
    <p>Atenciosamente,<br>Equipe ${empresa}</p>
</footer>
</body>
</html>