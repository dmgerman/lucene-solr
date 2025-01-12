begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Use by certain classes to match version compatibility  * across releases of Lucene.  *   *<p><b>WARNING</b>: When changing the version parameter  * that you supply to components in Lucene, do not simply  * change the version at search-time, but instead also adjust  * your indexing code to match, and re-index.  */
end_comment

begin_class
DECL|class|Version
specifier|public
specifier|final
class|class
name|Version
block|{
comment|/** Match settings and bugs in Lucene's 6.0 release.    * @deprecated (7.0.0) Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_0_0
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_0_0
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.0.1 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_0_1
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_0_1
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.1.0 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_1_0
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_1_0
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.2.0 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_2_0
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_2_0
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.2.1 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_2_1
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_2_1
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.3.0 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_3_0
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_3_0
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.4.0 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_4_0
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_4_0
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|4
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.4.1 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_4_1
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_4_1
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.4.2 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_4_2
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_4_2
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.5.0 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_5_0
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_5_0
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.5.1 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_5_1
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_5_1
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.6.0 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_6_0
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_6_0
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|6
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 6.7.0 release.    * @deprecated Use latest    */
annotation|@
name|Deprecated
DECL|field|LUCENE_6_7_0
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_6_7_0
init|=
operator|new
name|Version
argument_list|(
literal|6
argument_list|,
literal|7
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Match settings and bugs in Lucene's 7.0.0 release.    *<p>    *  Use this to get the latest&amp; greatest settings, bug    *  fixes, etc, for Lucene.    */
DECL|field|LUCENE_7_0_0
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_7_0_0
init|=
operator|new
name|Version
argument_list|(
literal|7
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// To add a new version:
comment|//  * Only add above this comment
comment|//  * If the new version is the newest, change LATEST below and deprecate the previous LATEST
comment|/**    *<p><b>WARNING</b>: if you use this setting, and then    * upgrade to a newer release of Lucene, sizable changes    * may happen.  If backwards compatibility is important    * then you should instead explicitly specify an actual    * version.    *<p>    * If you use this constant then you  may need to     *<b>re-index all of your documents</b> when upgrading    * Lucene, as the way text is indexed may have changed.     * Additionally, you may need to<b>re-test your entire    * application</b> to ensure it behaves as expected, as     * some defaults may have changed and may break functionality     * in your application.    */
DECL|field|LATEST
specifier|public
specifier|static
specifier|final
name|Version
name|LATEST
init|=
name|LUCENE_7_0_0
decl_stmt|;
comment|/**    * Constant for backwards compatibility.    * @deprecated Use {@link #LATEST}    */
annotation|@
name|Deprecated
DECL|field|LUCENE_CURRENT
specifier|public
specifier|static
specifier|final
name|Version
name|LUCENE_CURRENT
init|=
name|LATEST
decl_stmt|;
comment|/**    * Parse a version number of the form {@code "major.minor.bugfix.prerelease"}.    *    * Part {@code ".bugfix"} and part {@code ".prerelease"} are optional.    * Note that this is forwards compatible: the parsed version does not have to exist as    * a constant.    *    * @lucene.internal    */
DECL|method|parse
specifier|public
specifier|static
name|Version
name|parse
parameter_list|(
name|String
name|version
parameter_list|)
throws|throws
name|ParseException
block|{
name|StrictStringTokenizer
name|tokens
init|=
operator|new
name|StrictStringTokenizer
argument_list|(
name|version
argument_list|,
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokens
operator|.
name|hasMoreTokens
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Version is not in form major.minor.bugfix(.prerelease) (got: "
operator|+
name|version
operator|+
literal|")"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
name|int
name|major
decl_stmt|;
name|String
name|token
init|=
name|tokens
operator|.
name|nextToken
argument_list|()
decl_stmt|;
try|try
block|{
name|major
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|ParseException
name|p
init|=
operator|new
name|ParseException
argument_list|(
literal|"Failed to parse major version from \""
operator|+
name|token
operator|+
literal|"\" (got: "
operator|+
name|version
operator|+
literal|")"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|p
operator|.
name|initCause
argument_list|(
name|nfe
argument_list|)
expr_stmt|;
throw|throw
name|p
throw|;
block|}
if|if
condition|(
name|tokens
operator|.
name|hasMoreTokens
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Version is not in form major.minor.bugfix(.prerelease) (got: "
operator|+
name|version
operator|+
literal|")"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
name|int
name|minor
decl_stmt|;
name|token
operator|=
name|tokens
operator|.
name|nextToken
argument_list|()
expr_stmt|;
try|try
block|{
name|minor
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|ParseException
name|p
init|=
operator|new
name|ParseException
argument_list|(
literal|"Failed to parse minor version from \""
operator|+
name|token
operator|+
literal|"\" (got: "
operator|+
name|version
operator|+
literal|")"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|p
operator|.
name|initCause
argument_list|(
name|nfe
argument_list|)
expr_stmt|;
throw|throw
name|p
throw|;
block|}
name|int
name|bugfix
init|=
literal|0
decl_stmt|;
name|int
name|prerelease
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|tokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|token
operator|=
name|tokens
operator|.
name|nextToken
argument_list|()
expr_stmt|;
try|try
block|{
name|bugfix
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|ParseException
name|p
init|=
operator|new
name|ParseException
argument_list|(
literal|"Failed to parse bugfix version from \""
operator|+
name|token
operator|+
literal|"\" (got: "
operator|+
name|version
operator|+
literal|")"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|p
operator|.
name|initCause
argument_list|(
name|nfe
argument_list|)
expr_stmt|;
throw|throw
name|p
throw|;
block|}
if|if
condition|(
name|tokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|token
operator|=
name|tokens
operator|.
name|nextToken
argument_list|()
expr_stmt|;
try|try
block|{
name|prerelease
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|ParseException
name|p
init|=
operator|new
name|ParseException
argument_list|(
literal|"Failed to parse prerelease version from \""
operator|+
name|token
operator|+
literal|"\" (got: "
operator|+
name|version
operator|+
literal|")"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|p
operator|.
name|initCause
argument_list|(
name|nfe
argument_list|)
expr_stmt|;
throw|throw
name|p
throw|;
block|}
if|if
condition|(
name|prerelease
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Invalid value "
operator|+
name|prerelease
operator|+
literal|" for prerelease; should be 1 or 2 (got: "
operator|+
name|version
operator|+
literal|")"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
if|if
condition|(
name|tokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
comment|// Too many tokens!
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Version is not in form major.minor.bugfix(.prerelease) (got: "
operator|+
name|version
operator|+
literal|")"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
block|}
block|}
try|try
block|{
return|return
operator|new
name|Version
argument_list|(
name|major
argument_list|,
name|minor
argument_list|,
name|bugfix
argument_list|,
name|prerelease
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|ParseException
name|pe
init|=
operator|new
name|ParseException
argument_list|(
literal|"failed to parse version string \""
operator|+
name|version
operator|+
literal|"\": "
operator|+
name|iae
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|pe
operator|.
name|initCause
argument_list|(
name|iae
argument_list|)
expr_stmt|;
throw|throw
name|pe
throw|;
block|}
block|}
comment|/**    * Parse the given version number as a constant or dot based version.    *<p>This method allows to use {@code "LUCENE_X_Y"} constant names,    * or version numbers in the format {@code "x.y.z"}.    *    * @lucene.internal    */
DECL|method|parseLeniently
specifier|public
specifier|static
name|Version
name|parseLeniently
parameter_list|(
name|String
name|version
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|versionOrig
init|=
name|version
decl_stmt|;
name|version
operator|=
name|version
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|version
condition|)
block|{
case|case
literal|"LATEST"
case|:
case|case
literal|"LUCENE_CURRENT"
case|:
return|return
name|LATEST
return|;
default|default:
name|version
operator|=
name|version
operator|.
name|replaceFirst
argument_list|(
literal|"^LUCENE_(\\d+)_(\\d+)_(\\d+)$"
argument_list|,
literal|"$1.$2.$3"
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"^LUCENE_(\\d+)_(\\d+)$"
argument_list|,
literal|"$1.$2.0"
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"^LUCENE_(\\d)(\\d)$"
argument_list|,
literal|"$1.$2.0"
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|parse
argument_list|(
name|version
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
name|ParseException
name|pe2
init|=
operator|new
name|ParseException
argument_list|(
literal|"failed to parse lenient version string \""
operator|+
name|versionOrig
operator|+
literal|"\": "
operator|+
name|pe
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|pe2
operator|.
name|initCause
argument_list|(
name|pe
argument_list|)
expr_stmt|;
throw|throw
name|pe2
throw|;
block|}
block|}
block|}
comment|/** Returns a new version based on raw numbers    *    *  @lucene.internal */
DECL|method|fromBits
specifier|public
specifier|static
name|Version
name|fromBits
parameter_list|(
name|int
name|major
parameter_list|,
name|int
name|minor
parameter_list|,
name|int
name|bugfix
parameter_list|)
block|{
return|return
operator|new
name|Version
argument_list|(
name|major
argument_list|,
name|minor
argument_list|,
name|bugfix
argument_list|)
return|;
block|}
comment|/** Major version, the difference between stable and trunk */
DECL|field|major
specifier|public
specifier|final
name|int
name|major
decl_stmt|;
comment|/** Minor version, incremented within the stable branch */
DECL|field|minor
specifier|public
specifier|final
name|int
name|minor
decl_stmt|;
comment|/** Bugfix number, incremented on release branches */
DECL|field|bugfix
specifier|public
specifier|final
name|int
name|bugfix
decl_stmt|;
comment|/** Prerelease version, currently 0 (alpha), 1 (beta), or 2 (final) */
DECL|field|prerelease
specifier|public
specifier|final
name|int
name|prerelease
decl_stmt|;
comment|// stores the version pieces, with most significant pieces in high bits
comment|// ie:  | 1 byte | 1 byte | 1 byte |   2 bits   |
comment|//         major   minor    bugfix   prerelease
DECL|field|encodedValue
specifier|private
specifier|final
name|int
name|encodedValue
decl_stmt|;
DECL|method|Version
specifier|private
name|Version
parameter_list|(
name|int
name|major
parameter_list|,
name|int
name|minor
parameter_list|,
name|int
name|bugfix
parameter_list|)
block|{
name|this
argument_list|(
name|major
argument_list|,
name|minor
argument_list|,
name|bugfix
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|Version
specifier|private
name|Version
parameter_list|(
name|int
name|major
parameter_list|,
name|int
name|minor
parameter_list|,
name|int
name|bugfix
parameter_list|,
name|int
name|prerelease
parameter_list|)
block|{
name|this
operator|.
name|major
operator|=
name|major
expr_stmt|;
name|this
operator|.
name|minor
operator|=
name|minor
expr_stmt|;
name|this
operator|.
name|bugfix
operator|=
name|bugfix
expr_stmt|;
name|this
operator|.
name|prerelease
operator|=
name|prerelease
expr_stmt|;
comment|// NOTE: do not enforce major version so we remain future proof, except to
comment|// make sure it fits in the 8 bits we encode it into:
if|if
condition|(
name|major
operator|>
literal|255
operator|||
name|major
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal major version: "
operator|+
name|major
argument_list|)
throw|;
block|}
if|if
condition|(
name|minor
operator|>
literal|255
operator|||
name|minor
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal minor version: "
operator|+
name|minor
argument_list|)
throw|;
block|}
if|if
condition|(
name|bugfix
operator|>
literal|255
operator|||
name|bugfix
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal bugfix version: "
operator|+
name|bugfix
argument_list|)
throw|;
block|}
if|if
condition|(
name|prerelease
operator|>
literal|2
operator|||
name|prerelease
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal prerelease version: "
operator|+
name|prerelease
argument_list|)
throw|;
block|}
if|if
condition|(
name|prerelease
operator|!=
literal|0
operator|&&
operator|(
name|minor
operator|!=
literal|0
operator|||
name|bugfix
operator|!=
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Prerelease version only supported with major release (got prerelease: "
operator|+
name|prerelease
operator|+
literal|", minor: "
operator|+
name|minor
operator|+
literal|", bugfix: "
operator|+
name|bugfix
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|encodedValue
operator|=
name|major
operator|<<
literal|18
operator||
name|minor
operator|<<
literal|10
operator||
name|bugfix
operator|<<
literal|2
operator||
name|prerelease
expr_stmt|;
assert|assert
name|encodedIsValid
argument_list|()
assert|;
block|}
comment|/**    * Returns true if this version is the same or after the version from the argument.    */
DECL|method|onOrAfter
specifier|public
name|boolean
name|onOrAfter
parameter_list|(
name|Version
name|other
parameter_list|)
block|{
return|return
name|encodedValue
operator|>=
name|other
operator|.
name|encodedValue
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|prerelease
operator|==
literal|0
condition|)
block|{
return|return
literal|""
operator|+
name|major
operator|+
literal|"."
operator|+
name|minor
operator|+
literal|"."
operator|+
name|bugfix
return|;
block|}
return|return
literal|""
operator|+
name|major
operator|+
literal|"."
operator|+
name|minor
operator|+
literal|"."
operator|+
name|bugfix
operator|+
literal|"."
operator|+
name|prerelease
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|Version
operator|&&
operator|(
operator|(
name|Version
operator|)
name|o
operator|)
operator|.
name|encodedValue
operator|==
name|encodedValue
return|;
block|}
comment|// Used only by assert:
DECL|method|encodedIsValid
specifier|private
name|boolean
name|encodedIsValid
parameter_list|()
block|{
assert|assert
name|major
operator|==
operator|(
operator|(
name|encodedValue
operator|>>>
literal|18
operator|)
operator|&
literal|0xFF
operator|)
assert|;
assert|assert
name|minor
operator|==
operator|(
operator|(
name|encodedValue
operator|>>>
literal|10
operator|)
operator|&
literal|0xFF
operator|)
assert|;
assert|assert
name|bugfix
operator|==
operator|(
operator|(
name|encodedValue
operator|>>>
literal|2
operator|)
operator|&
literal|0xFF
operator|)
assert|;
assert|assert
name|prerelease
operator|==
operator|(
name|encodedValue
operator|&
literal|0x03
operator|)
assert|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|encodedValue
return|;
block|}
block|}
end_class

end_unit

