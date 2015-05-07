begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|BadPaddingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|Cipher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|IllegalBlockSizeException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|IvParameterSpec
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|SecretKeySpec
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|InvalidKeyException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PublicKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Signature
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SignatureException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|spec
operator|.
name|X509EncodedKeySpec
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|Base64
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**A utility class to verify signatures  *  */
end_comment

begin_class
DECL|class|CryptoKeys
specifier|public
specifier|final
class|class
name|CryptoKeys
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CryptoKeys
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|keys
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PublicKey
argument_list|>
name|keys
decl_stmt|;
DECL|field|exception
specifier|private
name|Exception
name|exception
decl_stmt|;
DECL|method|CryptoKeys
specifier|public
name|CryptoKeys
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|trustedKeys
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|PublicKey
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|e
range|:
name|trustedKeys
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|m
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|getX509PublicKey
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|keys
operator|=
name|m
expr_stmt|;
block|}
comment|/**    * Try with all signatures and return the name of the signature that matched    */
DECL|method|verify
specifier|public
name|String
name|verify
parameter_list|(
name|String
name|sig
parameter_list|,
name|ByteBuffer
name|data
parameter_list|)
block|{
name|exception
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PublicKey
argument_list|>
name|entry
range|:
name|keys
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|boolean
name|verified
decl_stmt|;
try|try
block|{
name|verified
operator|=
name|CryptoKeys
operator|.
name|verify
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|Base64
operator|.
name|base64ToByteArray
argument_list|(
name|sig
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"verified {} "
argument_list|,
name|verified
argument_list|)
expr_stmt|;
if|if
condition|(
name|verified
condition|)
return|return
name|entry
operator|.
name|getKey
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"NOT verified  "
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Create PublicKey from a .DER file    */
DECL|method|getX509PublicKey
specifier|public
specifier|static
name|PublicKey
name|getX509PublicKey
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|)
throws|throws
name|Exception
block|{
name|X509EncodedKeySpec
name|spec
init|=
operator|new
name|X509EncodedKeySpec
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|KeyFactory
name|kf
init|=
name|KeyFactory
operator|.
name|getInstance
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
return|return
name|kf
operator|.
name|generatePublic
argument_list|(
name|spec
argument_list|)
return|;
block|}
comment|/**    * Verify the signature of a file    *    * @param publicKey the public key used to sign this    * @param sig       the signature    * @param data      The data tha is signed    */
DECL|method|verify
specifier|public
specifier|static
name|boolean
name|verify
parameter_list|(
name|PublicKey
name|publicKey
parameter_list|,
name|byte
index|[]
name|sig
parameter_list|,
name|ByteBuffer
name|data
parameter_list|)
throws|throws
name|InvalidKeyException
throws|,
name|SignatureException
block|{
name|int
name|oldPos
init|=
name|data
operator|.
name|position
argument_list|()
decl_stmt|;
name|Signature
name|signature
init|=
literal|null
decl_stmt|;
try|try
block|{
name|signature
operator|=
name|Signature
operator|.
name|getInstance
argument_list|(
literal|"SHA1withRSA"
argument_list|)
expr_stmt|;
name|signature
operator|.
name|initVerify
argument_list|(
name|publicKey
argument_list|)
expr_stmt|;
name|signature
operator|.
name|update
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|boolean
name|verify
init|=
name|signature
operator|.
name|verify
argument_list|(
name|sig
argument_list|)
decl_stmt|;
return|return
name|verify
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
comment|//will not happen
block|}
finally|finally
block|{
comment|//Signature.update resets the position. set it back to old
name|data
operator|.
name|position
argument_list|(
name|oldPos
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|evpBytesTokey
specifier|private
specifier|static
name|byte
index|[]
index|[]
name|evpBytesTokey
parameter_list|(
name|int
name|key_len
parameter_list|,
name|int
name|iv_len
parameter_list|,
name|MessageDigest
name|md
parameter_list|,
name|byte
index|[]
name|salt
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|both
init|=
operator|new
name|byte
index|[
literal|2
index|]
index|[]
decl_stmt|;
name|byte
index|[]
name|key
init|=
operator|new
name|byte
index|[
name|key_len
index|]
decl_stmt|;
name|int
name|key_ix
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|iv
init|=
operator|new
name|byte
index|[
name|iv_len
index|]
decl_stmt|;
name|int
name|iv_ix
init|=
literal|0
decl_stmt|;
name|both
index|[
literal|0
index|]
operator|=
name|key
expr_stmt|;
name|both
index|[
literal|1
index|]
operator|=
name|iv
expr_stmt|;
name|byte
index|[]
name|md_buf
init|=
literal|null
decl_stmt|;
name|int
name|nkey
init|=
name|key_len
decl_stmt|;
name|int
name|niv
init|=
name|iv_len
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
return|return
name|both
return|;
block|}
name|int
name|addmd
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|md
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|addmd
operator|++
operator|>
literal|0
condition|)
block|{
name|md
operator|.
name|update
argument_list|(
name|md_buf
argument_list|)
expr_stmt|;
block|}
name|md
operator|.
name|update
argument_list|(
name|data
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|salt
condition|)
block|{
name|md
operator|.
name|update
argument_list|(
name|salt
argument_list|,
literal|0
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
name|md_buf
operator|=
name|md
operator|.
name|digest
argument_list|()
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|1
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|md
operator|.
name|reset
argument_list|()
expr_stmt|;
name|md
operator|.
name|update
argument_list|(
name|md_buf
argument_list|)
expr_stmt|;
name|md_buf
operator|=
name|md
operator|.
name|digest
argument_list|()
expr_stmt|;
block|}
name|i
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|nkey
operator|>
literal|0
condition|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|nkey
operator|==
literal|0
condition|)
break|break;
if|if
condition|(
name|i
operator|==
name|md_buf
operator|.
name|length
condition|)
break|break;
name|key
index|[
name|key_ix
operator|++
index|]
operator|=
name|md_buf
index|[
name|i
index|]
expr_stmt|;
name|nkey
operator|--
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|niv
operator|>
literal|0
operator|&&
name|i
operator|!=
name|md_buf
operator|.
name|length
condition|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|niv
operator|==
literal|0
condition|)
break|break;
if|if
condition|(
name|i
operator|==
name|md_buf
operator|.
name|length
condition|)
break|break;
name|iv
index|[
name|iv_ix
operator|++
index|]
operator|=
name|md_buf
index|[
name|i
index|]
expr_stmt|;
name|niv
operator|--
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nkey
operator|==
literal|0
operator|&&
name|niv
operator|==
literal|0
condition|)
block|{
break|break;
block|}
block|}
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|md_buf
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|md_buf
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|both
return|;
block|}
DECL|method|decodeAES
specifier|public
specifier|static
name|String
name|decodeAES
parameter_list|(
name|String
name|base64CipherTxt
parameter_list|,
name|String
name|pwd
parameter_list|)
block|{
name|int
index|[]
name|strengths
init|=
operator|new
name|int
index|[]
block|{
literal|256
block|,
literal|192
block|,
literal|128
block|}
decl_stmt|;
name|Exception
name|e
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|strength
range|:
name|strengths
control|)
block|{
try|try
block|{
return|return
name|decodeAES
argument_list|(
name|base64CipherTxt
argument_list|,
name|pwd
argument_list|,
name|strength
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exp
parameter_list|)
block|{
name|e
operator|=
name|exp
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Error decoding "
argument_list|,
name|e
argument_list|)
throw|;
block|}
DECL|method|decodeAES
specifier|public
specifier|static
name|String
name|decodeAES
parameter_list|(
name|String
name|base64CipherTxt
parameter_list|,
name|String
name|pwd
parameter_list|,
specifier|final
name|int
name|keySizeBits
parameter_list|)
block|{
specifier|final
name|Charset
name|ASCII
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"ASCII"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|INDEX_KEY
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|INDEX_IV
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|ITERATIONS
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|SALT_OFFSET
init|=
literal|8
decl_stmt|;
specifier|final
name|int
name|SALT_SIZE
init|=
literal|8
decl_stmt|;
specifier|final
name|int
name|CIPHERTEXT_OFFSET
init|=
name|SALT_OFFSET
operator|+
name|SALT_SIZE
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|headerSaltAndCipherText
init|=
name|Base64
operator|.
name|base64ToByteArray
argument_list|(
name|base64CipherTxt
argument_list|)
decl_stmt|;
comment|// --- extract salt& encrypted ---
comment|// header is "Salted__", ASCII encoded, if salt is being used (the default)
name|byte
index|[]
name|salt
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|headerSaltAndCipherText
argument_list|,
name|SALT_OFFSET
argument_list|,
name|SALT_OFFSET
operator|+
name|SALT_SIZE
argument_list|)
decl_stmt|;
name|byte
index|[]
name|encrypted
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|headerSaltAndCipherText
argument_list|,
name|CIPHERTEXT_OFFSET
argument_list|,
name|headerSaltAndCipherText
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// --- specify cipher and digest for evpBytesTokey method ---
name|Cipher
name|aesCBC
init|=
name|Cipher
operator|.
name|getInstance
argument_list|(
literal|"AES/CBC/PKCS5Padding"
argument_list|)
decl_stmt|;
name|MessageDigest
name|md5
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
decl_stmt|;
comment|// --- create key and IV  ---
comment|// the IV is useless, OpenSSL might as well have use zero's
specifier|final
name|byte
index|[]
index|[]
name|keyAndIV
init|=
name|evpBytesTokey
argument_list|(
name|keySizeBits
operator|/
name|Byte
operator|.
name|SIZE
argument_list|,
name|aesCBC
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|md5
argument_list|,
name|salt
argument_list|,
name|pwd
operator|.
name|getBytes
argument_list|(
name|ASCII
argument_list|)
argument_list|,
name|ITERATIONS
argument_list|)
decl_stmt|;
name|SecretKeySpec
name|key
init|=
operator|new
name|SecretKeySpec
argument_list|(
name|keyAndIV
index|[
name|INDEX_KEY
index|]
argument_list|,
literal|"AES"
argument_list|)
decl_stmt|;
name|IvParameterSpec
name|iv
init|=
operator|new
name|IvParameterSpec
argument_list|(
name|keyAndIV
index|[
name|INDEX_IV
index|]
argument_list|)
decl_stmt|;
comment|// --- initialize cipher instance and decrypt ---
name|aesCBC
operator|.
name|init
argument_list|(
name|Cipher
operator|.
name|DECRYPT_MODE
argument_list|,
name|key
argument_list|,
name|iv
argument_list|)
expr_stmt|;
name|byte
index|[]
name|decrypted
init|=
name|aesCBC
operator|.
name|doFinal
argument_list|(
name|encrypted
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|decrypted
argument_list|,
name|ASCII
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|BadPaddingException
name|e
parameter_list|)
block|{
comment|// AKA "something went wrong"
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Bad password, algorithm, mode or padding;"
operator|+
literal|" no salt, wrong number of iterations or corrupted ciphertext."
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalBlockSizeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Bad algorithm, mode or corrupted (resized) ciphertext."
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|GeneralSecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

