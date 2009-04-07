begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.trie
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|trie
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
name|util
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * This attribute is updated by {@link IntTrieTokenStream} and {@link LongTrieTokenStream}  * to the shift value of the current prefix-encoded token.  * It may be used by filters or consumers to e.g. distribute the values to various fields.  */
end_comment

begin_class
DECL|class|ShiftAttribute
specifier|public
specifier|final
class|class
name|ShiftAttribute
extends|extends
name|Attribute
implements|implements
name|Cloneable
implements|,
name|Serializable
block|{
DECL|field|shift
specifier|private
name|int
name|shift
init|=
literal|0
decl_stmt|;
comment|/**    * Returns the shift value of the current prefix encoded token.    */
DECL|method|getShift
specifier|public
name|int
name|getShift
parameter_list|()
block|{
return|return
name|shift
return|;
block|}
DECL|method|setShift
name|void
name|setShift
parameter_list|(
specifier|final
name|int
name|shift
parameter_list|)
block|{
name|this
operator|.
name|shift
operator|=
name|shift
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|shift
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"shift="
operator|+
name|shift
return|;
block|}
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
name|this
operator|==
name|other
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|other
operator|instanceof
name|ShiftAttribute
condition|)
block|{
return|return
operator|(
operator|(
name|ShiftAttribute
operator|)
name|other
operator|)
operator|.
name|shift
operator|==
name|shift
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|shift
return|;
block|}
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|Attribute
name|target
parameter_list|)
block|{
specifier|final
name|ShiftAttribute
name|t
init|=
operator|(
name|ShiftAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setShift
argument_list|(
name|shift
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

