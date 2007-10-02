begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 25-Jan-2006  */
end_comment

begin_package
DECL|package|org.apache.lucene.xmlparser.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
operator|.
name|builders
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
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|BooleanFilter
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
name|search
operator|.
name|DuplicateFilter
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
name|search
operator|.
name|Filter
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
name|search
operator|.
name|FilterClause
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
name|xmlparser
operator|.
name|DOMUtils
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
name|xmlparser
operator|.
name|FilterBuilder
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
name|xmlparser
operator|.
name|ParserException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * @author maharwood   */
end_comment

begin_class
DECL|class|DuplicateFilterBuilder
specifier|public
class|class
name|DuplicateFilterBuilder
implements|implements
name|FilterBuilder
block|{
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|String
name|fieldName
init|=
name|DOMUtils
operator|.
name|getAttributeWithInheritanceOrFail
argument_list|(
name|e
argument_list|,
literal|"fieldName"
argument_list|)
decl_stmt|;
name|DuplicateFilter
name|df
init|=
operator|new
name|DuplicateFilter
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|String
name|keepMode
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"keepMode"
argument_list|,
literal|"first"
argument_list|)
decl_stmt|;
if|if
condition|(
name|keepMode
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"first"
argument_list|)
condition|)
block|{
name|df
operator|.
name|setKeepMode
argument_list|(
name|DuplicateFilter
operator|.
name|KM_USE_FIRST_OCCURRENCE
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|keepMode
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"last"
argument_list|)
condition|)
block|{
name|df
operator|.
name|setKeepMode
argument_list|(
name|DuplicateFilter
operator|.
name|KM_USE_LAST_OCCURRENCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"Illegal keepMode attribute in DuplicateFilter:"
operator|+
name|keepMode
argument_list|)
throw|;
block|}
name|String
name|processingMode
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"processingMode"
argument_list|,
literal|"full"
argument_list|)
decl_stmt|;
if|if
condition|(
name|processingMode
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"full"
argument_list|)
condition|)
block|{
name|df
operator|.
name|setProcessingMode
argument_list|(
name|DuplicateFilter
operator|.
name|PM_FULL_VALIDATION
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|processingMode
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"fast"
argument_list|)
condition|)
block|{
name|df
operator|.
name|setProcessingMode
argument_list|(
name|DuplicateFilter
operator|.
name|PM_FAST_INVALIDATION
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"Illegal processingMode attribute in DuplicateFilter:"
operator|+
name|processingMode
argument_list|)
throw|;
block|}
return|return
name|df
return|;
block|}
block|}
end_class

end_unit

