begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.cn
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Analyzer
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
name|TokenStream
import|;
end_import

begin_comment
comment|/**  * Title: ChineseAnalyzer  * Description:  *   Subclass of org.apache.lucene.analysis.Analyzer  *   build from a ChineseTokenizer, filtered with ChineseFilter.  * Copyright:   Copyright (c) 2001  * Company:  * @author Yiyi Sun  * @version 1.0  *  */
end_comment

begin_class
DECL|class|ChineseAnalyzer
specifier|public
class|class
name|ChineseAnalyzer
extends|extends
name|Analyzer
block|{
DECL|method|ChineseAnalyzer
specifier|public
name|ChineseAnalyzer
parameter_list|()
block|{     }
comment|/**     * Creates a TokenStream which tokenizes all the text in the provided Reader.     *     * @return  A TokenStream build from a ChineseTokenizer filtered with ChineseFilter.     */
DECL|method|tokenStream
specifier|public
specifier|final
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|result
init|=
operator|new
name|ChineseTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|ChineseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

