begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|analysis
operator|.
name|Tokenizer
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
name|analysis
operator|.
name|standard
operator|.
name|StandardTokenizer
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
name|Version
import|;
end_import

begin_comment
comment|/**  * @version $Id$  * @deprecated Use {@link HTMLStripCharFilterFactory} and {@link StandardTokenizerFactory}  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|HTMLStripStandardTokenizerFactory
specifier|public
class|class
name|HTMLStripStandardTokenizerFactory
extends|extends
name|BaseTokenizerFactory
block|{
DECL|method|create
specifier|public
name|Tokenizer
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
return|return
operator|new
name|StandardTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|,
operator|new
name|HTMLStripReader
argument_list|(
name|input
argument_list|)
argument_list|)
return|;
comment|// nocommit: what to do about this?
comment|//    new HTMLStripReader(input)) {
comment|//      @Override
comment|//      public void reset(Reader reader) throws IOException {
comment|//        super.reset(new HTMLStripReader(reader));
comment|//      }
comment|//    };
block|}
block|}
end_class

end_unit

