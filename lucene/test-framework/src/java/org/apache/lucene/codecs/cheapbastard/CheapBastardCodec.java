begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.cheapbastard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|cheapbastard
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|FilterCodec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|PostingsFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import

begin_comment
comment|/** Codec that tries to use as little ram as possible because he spent all his money on beer */
end_comment

begin_comment
comment|// TODO: better name :)
end_comment

begin_comment
comment|// but if we named it "LowMemory" in codecs/ package, it would be irresistible like optimize()!
end_comment

begin_class
DECL|class|CheapBastardCodec
specifier|public
class|class
name|CheapBastardCodec
extends|extends
name|FilterCodec
block|{
comment|// TODO: would be better to have no terms index at all and bsearch a terms dict
DECL|field|postings
specifier|private
specifier|final
name|PostingsFormat
name|postings
init|=
name|TestUtil
operator|.
name|getDefaultPostingsFormat
argument_list|(
literal|100
argument_list|,
literal|200
argument_list|)
decl_stmt|;
DECL|method|CheapBastardCodec
specifier|public
name|CheapBastardCodec
parameter_list|()
block|{
name|super
argument_list|(
literal|"CheapBastard"
argument_list|,
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postingsFormat
specifier|public
name|PostingsFormat
name|postingsFormat
parameter_list|()
block|{
return|return
name|postings
return|;
block|}
block|}
end_class

end_unit

