begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.standard.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|config
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
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryParser
operator|.
name|standard
operator|.
name|processors
operator|.
name|AnalyzerQueryNodeProcessor
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
name|AttributeImpl
import|;
end_import

begin_comment
comment|/**  * This attribute is used by {@link AnalyzerQueryNodeProcessor} processor and  * must be defined in the {@link QueryConfigHandler}. It provides to this  * processor the {@link Analyzer}, if there is one, which will be used to  * analyze the query terms.<br/>  *   * @see org.apache.lucene.queryParser.standard.config.AnalyzerAttribute  */
end_comment

begin_class
DECL|class|AnalyzerAttributeImpl
specifier|public
class|class
name|AnalyzerAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|AnalyzerAttribute
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|method|AnalyzerAttributeImpl
specifier|public
name|AnalyzerAttributeImpl
parameter_list|()
block|{
name|analyzer
operator|=
literal|null
expr_stmt|;
comment|//default value 2.4
block|}
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|analyzer
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|AnalyzerAttributeImpl
condition|)
block|{
name|AnalyzerAttributeImpl
name|analyzerAttr
init|=
operator|(
name|AnalyzerAttributeImpl
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|analyzerAttr
operator|.
name|analyzer
operator|==
name|this
operator|.
name|analyzer
operator|||
operator|(
name|this
operator|.
name|analyzer
operator|!=
literal|null
operator|&&
name|analyzerAttr
operator|.
name|analyzer
operator|!=
literal|null
operator|&&
name|this
operator|.
name|analyzer
operator|.
name|equals
argument_list|(
name|analyzerAttr
operator|.
name|analyzer
argument_list|)
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
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
operator|(
name|this
operator|.
name|analyzer
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|this
operator|.
name|analyzer
operator|.
name|hashCode
argument_list|()
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
return|return
literal|"<analyzerAttribute analyzer='"
operator|+
name|this
operator|.
name|analyzer
operator|+
literal|"'/>"
return|;
block|}
block|}
end_class

end_unit

