begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|document
operator|.
name|Document
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
name|index
operator|.
name|Term
import|;
end_import

begin_comment
comment|/**  *  *  */
end_comment

begin_class
DECL|class|IndexDocumentStub
specifier|public
class|class
name|IndexDocumentStub
implements|implements
name|IndexDocument
block|{
DECL|field|document
name|Document
name|document
decl_stmt|;
DECL|field|deleteTerm
name|Term
name|deleteTerm
decl_stmt|;
DECL|field|action
name|IndexAction
name|action
decl_stmt|;
DECL|field|latch
name|CountDownLatch
name|latch
decl_stmt|;
DECL|field|commitAfter
name|boolean
name|commitAfter
decl_stmt|;
DECL|field|optimizeAfter
name|boolean
name|optimizeAfter
decl_stmt|;
comment|/**      *       */
DECL|method|IndexDocumentStub
specifier|public
name|IndexDocumentStub
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Term
name|deleteTerm
parameter_list|,
name|IndexAction
name|action
parameter_list|,
name|CountDownLatch
name|latch
parameter_list|)
block|{
name|this
operator|.
name|document
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|deleteTerm
operator|=
name|deleteTerm
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
DECL|method|IndexDocumentStub
specifier|public
name|IndexDocumentStub
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Term
name|deleteTerm
parameter_list|,
name|IndexAction
name|action
parameter_list|)
block|{
name|this
argument_list|(
name|doc
argument_list|,
name|deleteTerm
argument_list|,
name|action
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.search.index.IndexDocument#isUpdate()      */
DECL|method|isUpdate
specifier|public
name|boolean
name|isUpdate
parameter_list|()
block|{
return|return
name|isAction
argument_list|(
name|IndexAction
operator|.
name|UPDATE
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.search.index.IndexDocument#isDelete()      */
DECL|method|isDelete
specifier|public
name|boolean
name|isDelete
parameter_list|()
block|{
return|return
name|isAction
argument_list|(
name|IndexAction
operator|.
name|DELETE
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.search.index.IndexDocument#isInsert()      */
DECL|method|isInsert
specifier|public
name|boolean
name|isInsert
parameter_list|()
block|{
return|return
name|isAction
argument_list|(
name|IndexAction
operator|.
name|INSERT
argument_list|)
return|;
block|}
DECL|method|isAction
specifier|private
name|boolean
name|isAction
parameter_list|(
name|IndexAction
name|currentAction
parameter_list|)
block|{
return|return
name|this
operator|.
name|action
operator|==
name|currentAction
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.search.index.IndexDocument#getWriteable()      */
DECL|method|getWriteable
specifier|public
name|Document
name|getWriteable
parameter_list|()
block|{
if|if
condition|(
name|latch
operator|!=
literal|null
condition|)
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|document
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.search.index.IndexDocument#getDeletealbe()      */
DECL|method|getDeletealbe
specifier|public
name|Term
name|getDeletealbe
parameter_list|()
block|{
if|if
condition|(
name|latch
operator|!=
literal|null
condition|)
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|deleteTerm
return|;
block|}
DECL|method|commitAfter
specifier|public
name|boolean
name|commitAfter
parameter_list|()
block|{
return|return
name|this
operator|.
name|commitAfter
return|;
block|}
DECL|method|optimizeAfter
specifier|public
name|boolean
name|optimizeAfter
parameter_list|()
block|{
return|return
name|this
operator|.
name|optimizeAfter
return|;
block|}
comment|/**      * @param commitAfter The commitAfter to set.      */
DECL|method|setCommitAfter
specifier|public
name|void
name|setCommitAfter
parameter_list|(
name|boolean
name|commitAfter
parameter_list|)
block|{
name|this
operator|.
name|commitAfter
operator|=
name|commitAfter
expr_stmt|;
block|}
comment|/**      * @param optimizeAfter The optimizeAfter to set.      */
DECL|method|setOptimizeAfter
specifier|public
name|void
name|setOptimizeAfter
parameter_list|(
name|boolean
name|optimizeAfter
parameter_list|)
block|{
name|this
operator|.
name|optimizeAfter
operator|=
name|optimizeAfter
expr_stmt|;
block|}
comment|/**      * @see java.lang.Object#equals(java.lang.Object)      */
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
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
name|obj
operator|instanceof
name|IndexDocumentStub
condition|)
block|{
name|IndexDocumentStub
name|other
init|=
operator|(
name|IndexDocumentStub
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|document
operator|.
name|getField
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|)
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|document
operator|.
name|getField
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * @see java.lang.Object#hashCode()      */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|document
operator|.
name|getField
argument_list|(
name|IndexDocument
operator|.
name|FIELD_ENTRY_ID
argument_list|)
operator|.
name|stringValue
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

