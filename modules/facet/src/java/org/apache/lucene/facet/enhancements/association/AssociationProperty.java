begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.enhancements.association
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|enhancements
operator|.
name|association
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryAttribute
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryProperty
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A {@link CategoryProperty} associating a single integer value to a  * {@link CategoryAttribute}. It should be used to describe the association  * between the category and the document.  *<p>  * This class leave to extending classes the definition of  * {@link #merge(CategoryProperty)} policy for the integer associations.  *<p>  *<B>Note:</B> The association value is added both to a special category list,  * and to the category tokens.  *   * @see AssociationEnhancement  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AssociationProperty
specifier|public
specifier|abstract
class|class
name|AssociationProperty
implements|implements
name|CategoryProperty
block|{
DECL|field|association
specifier|protected
name|long
name|association
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|1
decl_stmt|;
comment|/**    * Construct an {@link AssociationProperty}.    *     * @param value    *            The association value.    */
DECL|method|AssociationProperty
specifier|public
name|AssociationProperty
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|association
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Returns the association value.    *     * @return The association value.    */
DECL|method|getAssociation
specifier|public
name|int
name|getAssociation
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|association
return|;
block|}
comment|/**    * Returns whether this attribute has been set (not all categories have an    * association).    */
DECL|method|hasBeenSet
specifier|public
name|boolean
name|hasBeenSet
parameter_list|()
block|{
return|return
name|this
operator|.
name|association
operator|<=
name|Integer
operator|.
name|MAX_VALUE
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
operator|+
name|association
return|;
block|}
block|}
end_class

end_unit

