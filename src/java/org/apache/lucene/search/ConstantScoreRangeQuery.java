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
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_comment
comment|/**  * A range query that returns a constant score equal to its boost for  * all documents in the range.  *<p>  * It does not have an upper bound on the number of clauses covered in the range.  *<p>  * If an endpoint is null, it is said to be "open".  * Either or both endpoints may be open.  Open endpoints may not be exclusive  * (you can't select all but the first or last term without explicitly specifying the term to exclude.)  *  * @deprecated Use {@link TermRangeQuery} for term ranges or  * {@link NumericRangeQuery} for numeric ranges instead.  * This class will be removed in Lucene 3.0.  * @version $Id$  */
end_comment

begin_class
DECL|class|ConstantScoreRangeQuery
specifier|public
class|class
name|ConstantScoreRangeQuery
extends|extends
name|TermRangeQuery
block|{
DECL|method|ConstantScoreRangeQuery
specifier|public
name|ConstantScoreRangeQuery
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|lowerVal
parameter_list|,
name|String
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|super
argument_list|(
name|fieldName
argument_list|,
name|lowerVal
argument_list|,
name|upperVal
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
expr_stmt|;
name|setConstantScoreRewrite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|ConstantScoreRangeQuery
specifier|public
name|ConstantScoreRangeQuery
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|lowerVal
parameter_list|,
name|String
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
name|super
argument_list|(
name|fieldName
argument_list|,
name|lowerVal
argument_list|,
name|upperVal
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|,
name|collator
argument_list|)
expr_stmt|;
name|setConstantScoreRewrite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getLowerVal
specifier|public
name|String
name|getLowerVal
parameter_list|()
block|{
return|return
name|getLowerTerm
argument_list|()
return|;
block|}
DECL|method|getUpperVal
specifier|public
name|String
name|getUpperVal
parameter_list|()
block|{
return|return
name|getUpperTerm
argument_list|()
return|;
block|}
block|}
end_class

end_unit

