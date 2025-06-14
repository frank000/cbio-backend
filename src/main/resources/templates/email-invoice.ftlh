<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Seu Invoice RayzaTEC</title>
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

        .invoice-info {
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 25px;
            margin: 25px 0;
            border-left: 4px solid #667eea;
        }

        .invoice-info h3 {
            color: #2c3e50;
            margin-bottom: 15px;
            font-size: 20px;
        }

        .invoice-detail {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            padding: 8px 0;
            border-bottom: 1px solid #e9ecef;
        }

        .invoice-detail:last-child {
            border-bottom: none;
        }

        .invoice-detail .label {
            font-weight: 600;
            color: #495057;
        }

        .invoice-detail .value {
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

            .invoice-detail {
                flex-direction: column;
                gap: 5px;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>✨ Invoice Disponível!</h1>
        <p>Seu pedido foi processado com sucesso</p>
    </div>

    <div class="content">
        <div class="greeting">
            Olá <strong>${nome}</strong>,
        </div>

        <p>Temos o prazer de informar que seu invoice foi gerado e está disponível para download. Confira os detalhes abaixo:</p>

        <div class="invoice-info">
            <h3>📋 Detalhes do Invoice</h3>
            <div class="invoice-detail">
                <span class="label">ID do Invoice:</span>
                <span class="value">${invoiceId}</span>
            </div>
            <div class="invoice-detail">
                <span class="label">Cliente:</span>
                <span class="value">${nome}</span>
            </div>
            <div class="invoice-detail">
                <span class="label">Status:</span>
                <span class="value">Disponível para Download</span>
            </div>
        </div>

        <div class="message">
            <p><strong>🎉 Seu invoice está pronto!</strong></p>
            <p>Clique em um dos botões abaixo para visualizar ou baixar seu invoice. Você pode acessá-lo online ou fazer o download em PDF.</p>
        </div>

        <div class="buttons-container">
            <#if urlInvoice??>
                <a href="${urlInvoice}" class="btn btn-primary">
                    🌐 Visualizar Online
                </a>
            </#if>
            <#if urlPDF??>
                <a href="${urlPDF}" class="btn btn-secondary">
                    📄 Download PDF
                </a>
            </#if>
        </div>

        <div style="text-align: center; margin: 30px 0;">
            <#if urlCancelamento??>
                <a href="${urlCancelamento}" class="btn" style="background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%); color: white; text-decoration: none; padding: 12px 25px; border-radius: 6px; font-size: 14px; font-weight: 500;">
                    🚫 Cancelar Pagamento Recorrente
                </a>
            </#if>
        </div>

        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 8px; padding: 20px; margin-top: 25px;">
            <p style="color: #856404; margin-bottom: 10px;"><strong>💡 Dicas importantes:</strong></p>
            <ul style="color: #856404; margin-left: 20px;">
                <li>Guarde este invoice para seus registros</li>
                <li>O link de acesso online é válido por 30 dias</li>
                <li>Em caso de dúvidas, entre em contato conosco</li>
            </ul>
        </div>
    </div>

    <div class="footer">
        <p class="company">RayzaTEC</p>
        <p>Obrigado por escolher nossos serviços!</p>
        <p style="font-size: 14px; opacity: 0.8;">Este é um email automático, não responda diretamente.</p>
    </div>
</div>
</body>
</html>