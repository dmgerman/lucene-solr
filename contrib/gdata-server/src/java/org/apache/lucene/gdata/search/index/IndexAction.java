begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * This enum defines all possible actions on a GData index.  *   * @see org.apache.lucene.gdata.search.index.IndexDocument  * @see org.apache.lucene.gdata.search.index.IndexDocumentBuilderTask  * @author Simon Willnauer  *   */
end_comment

begin_enum
DECL|enum|IndexAction
specifier|public
enum|enum
name|IndexAction
block|{
comment|/**      * update action      */
DECL|enum constant|UPDATE
name|UPDATE
block|,
comment|/**              * delete action              */
DECL|enum constant|DELETE
name|DELETE
block|,
comment|/**              * insert / add action              */
DECL|enum constant|INSERT
name|INSERT
block|}
end_enum

end_unit

