begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
package|;
end_package

begin_comment
comment|/**  * Copyright 2002-2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Low-level class used to record information about a section of a document   * with a score.  * @author MAHarwood  *  *   */
end_comment

begin_class
DECL|class|TextFragment
specifier|public
class|class
name|TextFragment
block|{
DECL|field|fragNum
name|int
name|fragNum
decl_stmt|;
DECL|field|textStartPos
name|int
name|textStartPos
decl_stmt|;
DECL|field|textEndPos
name|int
name|textEndPos
decl_stmt|;
DECL|field|score
name|float
name|score
decl_stmt|;
DECL|method|TextFragment
specifier|public
name|TextFragment
parameter_list|(
name|int
name|textStartPos
parameter_list|,
name|int
name|fragNum
parameter_list|)
block|{
name|this
operator|.
name|textStartPos
operator|=
name|textStartPos
expr_stmt|;
name|this
operator|.
name|fragNum
operator|=
name|fragNum
expr_stmt|;
block|}
DECL|method|setScore
name|void
name|setScore
parameter_list|(
name|float
name|score
parameter_list|)
block|{
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
DECL|method|getScore
specifier|public
name|float
name|getScore
parameter_list|()
block|{
return|return
name|score
return|;
block|}
comment|/** 	 * @param frag2 Fragment to be merged into this one 	 */
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|TextFragment
name|frag2
parameter_list|)
block|{
name|textEndPos
operator|=
name|frag2
operator|.
name|textEndPos
expr_stmt|;
block|}
comment|/** 	 * @param fragment  	 * @return true if this fragment follows the one passed 	 */
DECL|method|follows
specifier|public
name|boolean
name|follows
parameter_list|(
name|TextFragment
name|fragment
parameter_list|)
block|{
return|return
name|textStartPos
operator|==
name|fragment
operator|.
name|textEndPos
return|;
block|}
comment|/** 	 * @return the fragment sequence number 	 */
DECL|method|getFragNum
specifier|public
name|int
name|getFragNum
parameter_list|()
block|{
return|return
name|fragNum
return|;
block|}
block|}
end_class

end_unit

