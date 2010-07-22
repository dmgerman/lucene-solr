begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
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
name|NamedList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A base class for all analysis request handler tests.  *  * @version $Id$  * @since solr 1.4  */
end_comment

begin_class
DECL|class|AnalysisRequestHandlerTestBase
specifier|public
specifier|abstract
class|class
name|AnalysisRequestHandlerTestBase
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|assertToken
specifier|protected
name|void
name|assertToken
parameter_list|(
name|NamedList
name|token
parameter_list|,
name|TokenInfo
name|info
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|info
operator|.
name|getText
argument_list|()
argument_list|,
name|token
operator|.
name|get
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getRawText
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|info
operator|.
name|getRawText
argument_list|()
argument_list|,
name|token
operator|.
name|get
argument_list|(
literal|"raw_text"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|info
operator|.
name|getType
argument_list|()
argument_list|,
name|token
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
name|info
operator|.
name|getStart
argument_list|()
argument_list|)
argument_list|,
name|token
operator|.
name|get
argument_list|(
literal|"start"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
name|info
operator|.
name|getEnd
argument_list|()
argument_list|)
argument_list|,
name|token
operator|.
name|get
argument_list|(
literal|"end"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
name|info
operator|.
name|getPosition
argument_list|()
argument_list|)
argument_list|,
name|token
operator|.
name|get
argument_list|(
literal|"position"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|isMatch
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|token
operator|.
name|get
argument_list|(
literal|"match"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|getPayload
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|info
operator|.
name|getPayload
argument_list|()
argument_list|,
name|token
operator|.
name|get
argument_list|(
literal|"payload"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//================================================= Inner Classes ==================================================
DECL|class|TokenInfo
specifier|protected
class|class
name|TokenInfo
block|{
DECL|field|text
specifier|private
name|String
name|text
decl_stmt|;
DECL|field|rawText
specifier|private
name|String
name|rawText
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
decl_stmt|;
DECL|field|end
specifier|private
name|int
name|end
decl_stmt|;
DECL|field|payload
specifier|private
name|String
name|payload
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
decl_stmt|;
DECL|field|match
specifier|private
name|boolean
name|match
decl_stmt|;
DECL|method|TokenInfo
specifier|public
name|TokenInfo
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|rawText
parameter_list|,
name|String
name|type
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|position
parameter_list|,
name|String
name|payload
parameter_list|,
name|boolean
name|match
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|rawText
operator|=
name|rawText
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
name|this
operator|.
name|match
operator|=
name|match
expr_stmt|;
block|}
DECL|method|getText
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
DECL|method|getRawText
specifier|public
name|String
name|getRawText
parameter_list|()
block|{
return|return
name|rawText
return|;
block|}
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getStart
specifier|public
name|int
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
DECL|method|getEnd
specifier|public
name|int
name|getEnd
parameter_list|()
block|{
return|return
name|end
return|;
block|}
DECL|method|getPayload
specifier|public
name|String
name|getPayload
parameter_list|()
block|{
return|return
name|payload
return|;
block|}
DECL|method|getPosition
specifier|public
name|int
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
DECL|method|isMatch
specifier|public
name|boolean
name|isMatch
parameter_list|()
block|{
return|return
name|match
return|;
block|}
block|}
block|}
end_class

end_unit

