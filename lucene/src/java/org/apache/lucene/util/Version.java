begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Use by certain classes to match version compatibility  * across releases of Lucene.  *   *<p><b>WARNING</b>: When changing the version parameter  * that you supply to components in Lucene, do not simply  * change the version at search-time, but instead also adjust  * your indexing code to match, and re-index.  */
end_comment

begin_enum
DECL|enum|Version
specifier|public
enum|enum
name|Version
block|{
comment|/**    * Match settings and bugs in Lucene's 3.0 release.    * @deprecated (4.0) Use latest    */
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|LUCENE_30
name|LUCENE_30
block|,
comment|/**    * Match settings and bugs in Lucene's 3.1 release.    * @deprecated (4.0) Use latest    */
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|LUCENE_31
name|LUCENE_31
block|,
comment|/**    * Match settings and bugs in Lucene's 3.2 release.    * @deprecated (4.0) Use latest    */
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|LUCENE_32
name|LUCENE_32
block|,
comment|/**    * Match settings and bugs in Lucene's 3.3 release.    * @deprecated (4.0) Use latest    */
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|LUCENE_33
name|LUCENE_33
block|,
comment|/** Match settings and bugs in Lucene's 4.0 release.     *<p>    *  Use this to get the latest&amp; greatest settings, bug    *  fixes, etc, for Lucene.    */
DECL|enum constant|LUCENE_40
name|LUCENE_40
block|,
comment|/* Add new constants for later versions **here** to respect order! */
comment|/**    *<p><b>WARNING</b>: if you use this setting, and then    * upgrade to a newer release of Lucene, sizable changes    * may happen.  If backwards compatibility is important    * then you should instead explicitly specify an actual    * version.    *<p>    * If you use this constant then you  may need to     *<b>re-index all of your documents</b> when upgrading    * Lucene, as the way text is indexed may have changed.     * Additionally, you may need to<b>re-test your entire    * application</b> to ensure it behaves as expected, as     * some defaults may have changed and may break functionality     * in your application.     * @deprecated Use an actual version instead.     */
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|LUCENE_CURRENT
name|LUCENE_CURRENT
decl_stmt|;
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
name|compareTo
argument_list|(
name|other
argument_list|)
operator|>=
literal|0
return|;
block|}
block|}
end_enum

end_unit

