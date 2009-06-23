begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This interface is obsolete, use {@link FieldCache} instead.  * @deprecated Will be removed in Lucene 3.0  **/
end_comment

begin_interface
DECL|interface|ExtendedFieldCache
specifier|public
interface|interface
name|ExtendedFieldCache
extends|extends
name|FieldCache
block|{
comment|/** @deprecated Use {@link FieldCache#DEFAULT}; this will be removed in Lucene 3.0 */
DECL|field|EXT_DEFAULT
specifier|public
specifier|static
name|ExtendedFieldCache
name|EXT_DEFAULT
init|=
operator|(
name|ExtendedFieldCache
operator|)
name|FieldCache
operator|.
name|DEFAULT
decl_stmt|;
comment|/** @deprecated Use {@link FieldCache.LongParser}, this will be removed in Lucene 3.0 */
DECL|interface|LongParser
specifier|public
interface|interface
name|LongParser
extends|extends
name|FieldCache
operator|.
name|LongParser
block|{   }
comment|/** @deprecated Use {@link FieldCache.DoubleParser}, this will be removed in Lucene 3.0 */
DECL|interface|DoubleParser
specifier|public
interface|interface
name|DoubleParser
extends|extends
name|FieldCache
operator|.
name|DoubleParser
block|{   }
comment|/** @deprecated Will be removed in 3.0, this is for binary compatibility only */
DECL|method|getLongs
specifier|public
name|long
index|[]
name|getLongs
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|ExtendedFieldCache
operator|.
name|LongParser
name|parser
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** @deprecated Will be removed in 3.0, this is for binary compatibility only */
DECL|method|getDoubles
specifier|public
name|double
index|[]
name|getDoubles
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|ExtendedFieldCache
operator|.
name|DoubleParser
name|parser
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

