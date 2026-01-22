# ⚠️ ENCRYPTION KEY - KEEP SECURE!

**Generated on:** January 22, 2026 at 22:15:31

## Encryption Key (Base64)
```
0XIJ3cG1ZH2Ud4Pxq+o+LQVB8bsr9Z9o8zOX1iIf/4M=
```

## Important Instructions

### 🔒 Security
- **NEVER** commit this file to Git
- **NEVER** share this key publicly
- Store in a secure password manager or vault service

### 💾 How to Use This Key

#### Option 1: Environment Variable (Recommended for Production)
```bash
# Windows PowerShell
$env:ENCRYPTION_KEY="0XIJ3cG1ZH2Ud4Pxq+o+LQVB8bsr9Z9o8zOX1iIf/4M="

# Linux/Mac
export ENCRYPTION_KEY="0XIJ3cG1ZH2Ud4Pxq+o+LQVB8bsr9Z9o8zOX1iIf/4M="
```

#### Option 2: Application Properties (For Development Only)
Add to `application.properties`:
```properties
encryption.key=0XIJ3cG1ZH2Ud4Pxq+o+LQVB8bsr9Z9o8zOX1iIf/4M=
```

Then modify `EncryptionUtil.java` constructor to load from config.

### ⚠️ What Happens If You Lose This Key?
- All encrypted usernames and emails become **unrecoverable**
- You will need to **recreate all user accounts**
- Database backup won't help without the key

### 📋 Backup Checklist
- [ ] Key saved in password manager
- [ ] Key stored in production vault (AWS KMS, Azure Key Vault, etc.)
- [ ] Team members with access know where to find it
- [ ] Documented in secure internal wiki/docs

---

**Date Generated:** January 22, 2026  
**Purpose:** AES-256-GCM encryption for user data  
**Used For:** Encrypting username and email fields in users table

