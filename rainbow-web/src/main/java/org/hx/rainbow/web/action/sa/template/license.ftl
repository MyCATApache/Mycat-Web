<?xml version="1.0" encoding="UTF-8"?>
<licenses>
    <license code="${licenseCode}">
        <customerCode desc="客户代码">${customerCode}</customerCode>
        <customerName desc="客户名称">${customerName}</customerName>
        <productCompany desc="授权企业">${productCompany}</productCompany>
        <productCode desc="项目代码">${productCode}</productCode>
        <productName desc="项目名称">${productName}</productName>
        <expiringDate desc="过期时间">${(expiringDate?string('yyyy-MM-dd'))!}</expiringDate>
        <licVersion desc="许可证版本">${licVersion}</licVersion>
        <licenseCode desc="产品代码">${licenseCode}</licenseCode>
        <licenseMode desc="许可证模式">${licenseMode}</licenseMode>
        <signingDate desc="签发时间">${(signingDate?string('yyyy-MM-dd'))!}</signingDate>
        <startDate desc="起始时间">${(startDate?string('yyyy-MM-dd'))!}</startDate>
        <versionNumber desc="产品版本" >${versionNumber}</versionNumber>
        <signature desc="防伪签名" >${signature}</signature>
    </license>
</licenses>
