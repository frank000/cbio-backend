<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bem-vindo à RayzaTEC</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f8f9fa;
        }

        .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: #ffffff;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        }

        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px 30px;
            text-align: center;
        }

        .header h1 {
            font-size: 28px;
            margin-bottom: 10px;
            font-weight: 700;
        }

        .header p {
            font-size: 16px;
            opacity: 0.9;
        }

        .content {
            padding: 40px 30px;
        }

        .greeting {
            font-size: 18px;
            color: #2c3e50;
            margin-bottom: 25px;
        }

        .welcome-info {
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 25px;
            margin: 25px 0;
            border-left: 4px solid #667eea;
        }

        .welcome-info h3 {
            color: #2c3e50;
            margin-bottom: 15px;
            font-size: 20px;
        }

        .account-detail {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            padding: 8px 0;
            border-bottom: 1px solid #e9ecef;
        }

        .account-detail:last-child {
            border-bottom: none;
        }

        .account-detail .label {
            font-weight: 600;
            color: #495057;
        }

        .account-detail .value {
            color: #667eea;
            font-weight: 500;
        }

        .buttons-container {
            text-align: center;
            margin: 35px 0;
        }

        .btn {
            display: inline-block;
            padding: 15px 30px;
            margin: 10px;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
            font-size: 16px;
            transition: all 0.3s ease;
            min-width: 180px;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .btn-secondary {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
        }

        .message {
            background-color: #e3f2fd;
            border-radius: 8px;
            padding: 20px;
            margin: 25px 0;
            border-left: 4px solid #2196f3;
        }

        .message p {
            color: #1565c0;
            margin-bottom: 10px;
        }

        .dunamis-info {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 8px;
            padding: 25px;
            margin: 25px 0;
        }

        .dunamis-info h3 {
            color: white;
            margin-bottom: 15px;
            font-size: 20px;
        }

        .dunamis-info p {
            color: rgba(255, 255, 255, 0.9);
            margin-bottom: 10px;
        }

        .credentials-box {
            background-color: #fff3cd;
            border: 2px solid #ffeaa7;
            border-radius: 8px;
            padding: 20px;
            margin: 25px 0;
        }

        .credentials-box h4 {
            color: #856404;
            margin-bottom: 15px;
            font-size: 18px;
        }

        .credential-item {
            background-color: white;
            border: 1px solid #dee2e6;
            border-radius: 4px;
            padding: 12px;
            margin-bottom: 10px;
            font-family: 'Courier New', monospace;
        }

        .credential-label {
            font-weight: 600;
            color: #495057;
            font-size: 14px;
        }

        .credential-value {
            color: #667eea;
            font-weight: 500;
            word-break: break-all;
        }

        .footer {
            background-color: #2c3e50;
            color: white;
            padding: 30px;
            text-align: center;
        }

        .footer p {
            margin-bottom: 10px;
        }

        .footer .company {
            font-size: 18px;
            font-weight: 600;
            color: #667eea;
        }

        @media (max-width: 600px) {
            .container {
                margin: 10px;
                border-radius: 8px;
            }

            .header, .content, .footer {
                padding: 25px 20px;
            }

            .btn {
                display: block;
                margin: 10px 0;
                width: 100%;
            }

            .account-detail {
                flex-direction: column;
                gap: 5px;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>🎉 Bem-vindo à RayzaTEC!</h1>
        <p>Sua jornada de sucesso começa agora</p>
    </div>

    <div class="content">
        <div class="greeting">
            Olá, equipe da <strong>${companyName}</strong>!
        </div>

        <p>É com imenso prazer que damos as boas-vindas à <strong>RayzaTEC</strong>! A partir de agora, iniciamos uma parceria que transformará a forma como vocês se conectam com seus clientes.</p>

        <div class="message">
            <p><strong>🤝 O seu sucesso é o nosso sucesso!</strong></p>
            <p>Estamos empenhados em oferecer o melhor atendimento e suporte para que vocês alcancem resultados extraordinários. Nossa equipe está dedicada ao crescimento da sua empresa.</p>
        </div>

        <div class="welcome-info">
            <h3>📊 Detalhes da sua Conta</h3>
            <div class="account-detail">
                <span class="label">Empresa:</span>
                <span class="value">${companyName}</span>
            </div>
            <div class="account-detail">
                <span class="label">Plano Contratado:</span>
                <span class="value">${tier}</span>
            </div>
            <div class="account-detail">
                <span class="label">Modalidade:</span>
                <span class="value">
                        <#if modo == "trial">
                            🔍 Período de Avaliação
                        <#else>
                            💳 Pagamento Recorrente
                        </#if>
                    </span>
            </div>
            <div class="account-detail">
                <span class="label">Status:</span>
                <span class="value">✅ Ativo</span>
            </div>
        </div>

        <div class="dunamis-info">
            <h3>🤖 Conheça o Dunamis</h3>
            <p><strong>O Dunamis</strong> é uma aplicação revolucionária que integra as mais novas tecnologias de <strong>inteligência artificial</strong> e <strong>redes neurais</strong> com gestão avançada de comunicação.</p>
            <p>Aliamos toda essa tecnologia à praticidade das redes sociais, proporcionando um <strong>alto número de retenção de clientes finais</strong> para nossos parceiros.</p>
            <p>Com o Dunamis, vocês terão em mãos uma ferramenta poderosa que transforma a experiência do cliente e impulsiona o crescimento do seu negócio!</p>
        </div>

        <div class="credentials-box">
            <h4>🔑 Suas Credenciais de Acesso</h4>
            <p style="color: #856404; margin-bottom: 15px;">Guarde estas informações com segurança:</p>

            <div class="credential-item">
                <div class="credential-label">Email do Administrador:</div>
                <div class="credential-value">${emailAdministrador}</div>
            </div>

            <div class="credential-item">
                <div class="credential-label">Senha Temporária:</div>
                <div class="credential-value">${passwordAdministrador}</div>
            </div>

            <p style="color: #856404; font-size: 14px; margin-top: 15px;">
                <strong>⚠️ Importante:</strong> Recomendamos alterar sua senha no primeiro acesso para garantir a segurança da sua conta.
            </p>
        </div>

        <div class="buttons-container">
            <a href="#" class="btn btn-primary">
                🚀 Acessar Dunamis
            </a>
            <a href="#" class="btn btn-secondary">
                📚 Central de Ajuda
            </a>
        </div>

        <div style="background-color: #d1ecf1; border: 1px solid #bee5eb; border-radius: 8px; padding: 20px; margin-top: 25px;">
            <p style="color: #0c5460; margin-bottom: 10px;"><strong>🎯 Próximos Passos:</strong></p>
            <ul style="color: #0c5460; margin-left: 20px;">
                <li>Faça seu primeiro login e altere sua senha</li>
                <li>Explore as funcionalidades do Dunamis</li>
                <li>Configure suas primeiras automações</li>
                <li>Entre em contato conosco se precisar de suporte</li>
            </ul>
        </div>
    </div>

    <div class="footer">
        <p class="company">RayzaTEC</p>
        <p>Juntos, vamos transformar sua comunicação com clientes!</p>
        <p style="font-size: 14px; opacity: 0.8;">Em caso de dúvidas, nossa equipe está sempre disponível para ajudar.</p>
    </div>
</div>
</body>
</html>