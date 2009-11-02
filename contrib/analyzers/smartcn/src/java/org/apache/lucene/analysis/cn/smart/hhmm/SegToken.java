begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn.smart.hhmm
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
operator|.
name|smart
operator|.
name|hhmm
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|cn
operator|.
name|smart
operator|.
name|WordType
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_comment
comment|/**  * SmartChineseAnalyzer internal token  *<p><font color="#FF0000">  * WARNING: The status of the analyzers/smartcn<b>analysis.cn.smart</b> package is experimental.   * The APIs and file formats introduced here might change in the future and will not be   * supported anymore in such a case.</font>  *</p>  */
end_comment

begin_class
DECL|class|SegToken
specifier|public
class|class
name|SegToken
block|{
comment|/**    * Character array containing token text    */
DECL|field|charArray
specifier|public
name|char
index|[]
name|charArray
decl_stmt|;
comment|/**    * start offset into original sentence    */
DECL|field|startOffset
specifier|public
name|int
name|startOffset
decl_stmt|;
comment|/**    * end offset into original sentence    */
DECL|field|endOffset
specifier|public
name|int
name|endOffset
decl_stmt|;
comment|/**    * {@link WordType} of the text     */
DECL|field|wordType
specifier|public
name|int
name|wordType
decl_stmt|;
comment|/**    * word frequency    */
DECL|field|weight
specifier|public
name|int
name|weight
decl_stmt|;
comment|/**    * during segmentation, this is used to store the index of the token in the token list table    */
DECL|field|index
specifier|public
name|int
name|index
decl_stmt|;
comment|/**    * Create a new SegToken from a character array.    *     * @param idArray character array containing text    * @param start start offset of SegToken in original sentence    * @param end end offset of SegToken in original sentence    * @param wordType {@link WordType} of the text    * @param weight word frequency    */
DECL|method|SegToken
specifier|public
name|SegToken
parameter_list|(
name|char
index|[]
name|idArray
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|wordType
parameter_list|,
name|int
name|weight
parameter_list|)
block|{
name|this
operator|.
name|charArray
operator|=
name|idArray
expr_stmt|;
name|this
operator|.
name|startOffset
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|wordType
operator|=
name|wordType
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
comment|/**    * @see java.lang.Object#hashCode()    */
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|charArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|charArray
index|[
name|i
index|]
expr_stmt|;
block|}
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|endOffset
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|index
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|startOffset
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|weight
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|wordType
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * @see java.lang.Object#equals(java.lang.Object)    */
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|SegToken
name|other
init|=
operator|(
name|SegToken
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|charArray
argument_list|,
name|other
operator|.
name|charArray
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|endOffset
operator|!=
name|other
operator|.
name|endOffset
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|index
operator|!=
name|other
operator|.
name|index
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|startOffset
operator|!=
name|other
operator|.
name|startOffset
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|weight
operator|!=
name|other
operator|.
name|weight
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|wordType
operator|!=
name|other
operator|.
name|wordType
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

