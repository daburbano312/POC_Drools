# Drools DSL - Reglas de Negocio en Lenguaje Natural
# Este archivo define un DSL para expresar reglas de préstamos de forma legible para el negocio

[keyword]
when The customer age is less than {age}=When Customer(age < {age})
when The customer age is greater than {age}=When Customer(age > {age})
when The customer credit score is {score}=When Customer(creditScore == {score})
when The customer credit score is at least {score}=When Customer(creditScore >= {score})
when The customer credit score is below {score}=When Customer(creditScore < {score})
when The customer monthly income is less than {amount}=When Customer(monthlyIncome < {amount})
when The customer monthly income is greater than {amount}=When Customer(monthlyIncome > {amount})
when The loan amount is greater than {amount}=When LoanApplication(loanAmount > {amount})
when The loan term is {months} months=When LoanApplication(loanTerm == {months})
when The customer is unemployed=When Customer(employmentStatus == "UNEMPLOYED")
when The customer is self-employed=When Customer(employmentStatus == "SELF_EMPLOYED")
when The customer has savings=When Customer(hasSavings == true, savingsAmount > 0)
when The customer is politically exposed=When Customer(isPoliticallyExposed == true)
when The risk score is {score}=When RiskAssessment(riskScore >= {score})

[consequence]
Set risk level to HIGH=risk.setRiskLevel("HIGH");
Set risk level to LOW=risk.setRiskLevel("LOW");
Set risk level to MEDIUM=risk.setRiskLevel("MEDIUM");
Set risk level to CRITICAL=risk.setRiskLevel("CRITICAL");
Increase risk score by {amount}=risk.setRiskScore(risk.getRiskScore() + {amount});
Decrease risk score by {amount}=risk.setRiskScore(Math.max(0, risk.getRiskScore() - {amount}));
Require manual review=risk.setRequiresManualReview(true);
Add risk factor {factor}=risk.addRiskFactor("{factor}");
Reject application=decision.setStatus("REJECTED");
Approve application=decision.setStatus("APPROVED");
Send to manual review=decision.setStatus("PENDING_REVIEW");
Set approved amount to {amount}=decision.setApprovedAmount({amount});
Set interest rate to {rate}=decision.setApprovedInterestRate({rate});
Add required document {doc}=risk.addRequiredDocument("{doc}");
Update risk assessment=update(risk);
Update decision=update(decision);
