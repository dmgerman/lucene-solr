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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Expert: A ScoreDoc which also contains information about  * how to sort the referenced document.  In addition to the  * document number and score, this object contains an array  * of values for the document from the field(s) used to sort.  * For example, if the sort criteria was to sort by fields  * "a", "b" then "c", the<code>fields</code> object array  * will have three elements, corresponding respectively to  * the term values for the document in fields "a", "b" and "c".  * The class of each element in the array will be either  * Integer, Float or String depending on the type of values  * in the terms of each field.  *  *<p>Created: Feb 11, 2004 1:23:38 PM  *  * @author  Tim Jones (Nacimiento Software)  * @since   lucene 1.4  * @version $Id$  * @see ScoreDoc  * @see TopFieldDocs  */
end_comment

begin_class
DECL|class|FieldDoc
specifier|public
class|class
name|FieldDoc
extends|extends
name|ScoreDoc
block|{
comment|/** Expert: The values which are used to sort the referenced document. 	 * The order of these will match the original sort criteria given by a 	 * Sort object.  Each Object will be either an Integer, Float or String, 	 * depending on the type of values in the terms of the original field. 	 * @see Sort 	 * @see Searchable#search(Query,Filter,int,Sort) 	 */
DECL|field|fields
specifier|public
name|Object
index|[]
name|fields
decl_stmt|;
comment|/** Expert: Creates one of these objects with empty sort information. */
DECL|method|FieldDoc
specifier|public
name|FieldDoc
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|super
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: Creates one of these objects with the given sort information. */
DECL|method|FieldDoc
specifier|public
name|FieldDoc
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|,
name|Object
index|[]
name|fields
parameter_list|)
block|{
name|super
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
block|}
end_class

end_unit

