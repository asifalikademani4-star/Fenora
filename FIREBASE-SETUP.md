# Finora online account setup

The app now contains a phone OTP sign-in and per-user Firestore sync scaffold. **Online sign-in is not active until you configure your own Firebase project.** Never place a Firebase service-account key in the Android app or repository.

## 1. Create and configure Firebase
1. In the Firebase console, create a project and register a Web App.
2. Enable **Authentication → Sign-in method → Phone**. Configure SMS regions and test phone numbers where appropriate.
3. Enable **Cloud Firestore**.
4. Copy the Web App config values into `firebaseConfig` in `app/src/main/assets/index.html` (replace all `REPLACE_...` values).
5. Test on a real device. WebView reCAPTCHA/phone auth may require extra testing; if it fails, implement native Firebase Authentication in Android rather than bypassing verification.

## 2. Firestore security rules
Publish the rules in `firestore.rules`. They restrict each user to their own `users/{uid}` document. Do not use permissive test-mode rules for real financial data.

## 3. Bank statements and automatic connections
- Phone OTP proves control of a phone number; it does **not** reveal a user's employer, salary, bank statement, loans or outstanding balance.
- Automatic bank data in India requires an authorized Account Aggregator or bank-data partner, user consent, partner credentials, secure backend endpoints and compliance/security review. Lender-side loan details also require a lender integration or user-uploaded loan statement. No provider credentials or live bank/lender integration are configured here, so automatic data retrieval is not active yet.
- Loan creation now collects lender, principal, interest, EMI, tenure, EMIs paid and outstanding principal. These are user-provided figures until reconciled with an official lender statement.
- Manual bank-statement parsing is a separate step; current backup import is for Finora JSON backups, not bank statements.

## 4. Production readiness
Before real financial data, review Firestore rules, OTP abuse limits, account deletion, data export, privacy practices and security. Never upload bank passwords, OTPs, card PINs, UPI PINs or net-banking credentials to Finora.
