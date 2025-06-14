<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cancelamento Confirmado - RayzaTEC</title>
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
            background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
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

        .cancellation-info {
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 25px;
            margin: 25px 0;
            border-left: 4px solid #6c757d;
        }

        .cancellation-info h3 {
            color: #2c3e50;
            margin-bottom: 15px;
            font-size: 20px;
        }

        .cancellation-detail {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            padding: 8px 0;
            border-bottom: 1px solid #e9ecef;
        }

        .cancellation-detail:last-child {
            border-bottom: none;
        }

        .cancellation-detail .label {
            font-weight: 600;
            color: #495057;
        }

        .cancellation-detail .value {
            color: #6c757d;
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
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
        }

        .btn-secondary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
        }

        .message {
            background-color: #fff3cd;
            border-radius: 8px;
            padding: 20px;
            margin: 25px 0;
            border-left: 4px solid #ffc107;
        }

        .message p {
            color: #856404;
            margin-bottom: 10px;
        }

        .sad-message {
            background-color: #f8d7da;
            border-radius: 8px;
            padding: 20px;
            margin: 25px 0;
            border-left: 4px solid #dc3545;
        }

        .sad-message p {
            color: #721c24;
            margin-bottom: 10px;
        }

        .feedback-box {
            background: linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%);
            border-radius: 8px;
            padding: 25px;
            margin: 25px 0;
            border: 1px solid #e1bee7;
        }

        .feedback-box h3 {
            color: #4a148c;
            margin-bottom: 15px;
            font-size: 20px;
        }

        .feedback-box p {
            color: #6a1b9a;
            margin-bottom: 10px;
        }

        .important-info {
            background-color: #d1ecf1;
            border: 1px solid #bee5eb;
            border-radius: 8px;
            padding: 20px;
            margin: 25px 0;
        }

        .important-info h4 {
            color: #0c5460;
            margin-bottom: 15px;
            font-size: 18px;
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

            .cancellation-detail {
                flex-direction: column;
                gap: 5px;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>✅ Cancelamento Confirmado</h1>
        <p>Sua solicitação foi processada com sucesso</p>
    </div>

    <div class="content">
        <div class="greeting">
            Olá, equipe da <strong>${companyName}</strong>,
        </div>

        <p>Confirmamos que sua solicitação de cancelamento foi processada com sucesso. Lamentamos ver vocês partirem e esperamos que nossa parceria tenha agregado valor ao seu negócio.</p>

        <div class="sad-message">
            <p><strong>😔 Sentiremos sua falta!</strong></p>
            <p>Foi um prazer trabalhar com a <strong>${companyName}</strong>. Esperamos que os resultados obtidos com o Dunamis tenham contribuído para o crescimento da sua empresa.</p>
        </div>

        <div class="cancellation-info">
            <h3>📋 Detalhes do Cancelamento</h3>
            <div class="cancellation-detail">
                <span class="label">Empresa:</span>
                <span class="value">${companyName}</span>
            </div>
            <div class="cancellation-detail">
                <span class="label">Plano Cancelado:</span>
                <span class="value">${tier}</span>
            </div>
            <div class="cancellation-detail">
                <span class="label">Data do Cancelamento:</span>
                <span class="value">${dataCancelamento}</span>
            </div>
            <div class="cancellation-detail">
                <span class="label">Último Dia de Acesso:</span>
                <span class="value">${dataFinalAcesso}</span>
            </div>
            <div class="cancellation-detail">
                <span class="label">Status:</span>
                <span class="value">❌ Cancelado</span>
            </div>
        </div>

        <div class="important-info">
            <h4>📅 Informações Importantes</h4>
            <ul style="color: #0c5460; margin-left: 20px;">
                <li><strong>Acesso:</strong> Você terá acesso completo ao Dunamis até <strong>${dataFinalAcesso}</strong></li>
                <li><strong>Dados:</strong> Recomendamos fazer backup de dados importantes antes do vencimento</li>
                <li><strong>Cobrança:</strong> Não haverá mais cobranças recorrentes a partir de agora</li>
                <li><strong>Reativação:</strong> Você pode reativar sua conta a qualquer momento</li>
            </ul>
        </div>

        <div class="feedback-box">
            <h3>💬 Sua Opinião é Importante</h3>
            <p>Gostaríamos muito de saber como podemos melhorar nossos serviços. Sua experiência e feedback são fundamentais para nossa evolução.</p>
            <p>Se tiver alguns minutos, adoraríamos ouvir sobre sua experiência conosco!</p>
        </div>

        <div class="buttons-container">
            <a href="#" class="btn btn-primary">
                📝 Deixar Feedback
            </a>
            <a href="#" class="btn btn-secondary">
                🔄 Reativar Conta
            </a>
        </div>

        <div class="message">
            <p><strong>🚪 A porta está sempre aberta!</strong></p>
            <p>Se mudarem de ideia ou precisarem de nossos serviços no futuro, estaremos aqui de braços abertos. Basta entrar em contato conosco!</p>
        </div>

        <div style="background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 8px; padding: 20px; margin-top: 25px;">
            <p style="color: #155724; margin-bottom: 10px;"><strong>📞 Precisa de Ajuda?</strong></p>
            <ul style="color: #155724; margin-left: 20px;">
                <li>Nossa equipe de suporte está disponível para esclarecer dúvidas</li>
                <li>Podemos ajudar com backup de dados ou migração</li>
                <li>Entre em contato se precisar de assistência</li>
            </ul>
        </div>
    </div>

    <div class="footer">
        <p class="company">RayzaTEC</p>
        <p>Obrigado por ter confiado em nossos serviços!</p>
        <p style="font-size: 14px; opacity: 0.8;">Esperamos vê-los novamente em breve. Sucesso em seus projetos!</p>
    </div>
</div>
</body>
</html>