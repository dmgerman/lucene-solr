begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|TokenStream
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
name|document
operator|.
name|Field
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
name|FieldType
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
name|TextField
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
name|DocumentBuilder
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
name|CategoryAttributesIterable
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
name|categorypolicy
operator|.
name|OrdinalPolicy
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
name|categorypolicy
operator|.
name|PathPolicy
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
name|params
operator|.
name|DefaultFacetIndexingParams
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
name|params
operator|.
name|FacetIndexingParams
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
name|streaming
operator|.
name|CategoryAttributesStream
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
name|streaming
operator|.
name|CategoryListTokenizer
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
name|streaming
operator|.
name|CategoryParentsStream
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
name|streaming
operator|.
name|CategoryTokenizer
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
name|streaming
operator|.
name|CountingListTokenizer
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
name|taxonomy
operator|.
name|CategoryPath
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
name|taxonomy
operator|.
name|TaxonomyWriter
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A utility class which allows attachment of {@link CategoryPath}s or  * {@link CategoryAttribute}s to a given document using a taxonomy.<br>  * Construction could be done with either a given {@link FacetIndexingParams} or  * the default implementation {@link DefaultFacetIndexingParams}.<br>  * A CategoryDocumentBuilder can be reused by repeatedly setting the categories  * and building the document. Categories are provided either as  * {@link CategoryAttribute} elements through {@link #setCategories(Iterable)},  * or as {@link CategoryPath} elements through  * {@link #setCategoryPaths(Iterable)}.  *<p>  * Note that both {@link #setCategories(Iterable)} and  * {@link #setCategoryPaths(Iterable)} return this  * {@link CategoryDocumentBuilder}, allowing the following pattern: {@code new  * CategoryDocumentBuilder(taxonomy,  * params).setCategories(categories).build(doc)}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CategoryDocumentBuilder
specifier|public
class|class
name|CategoryDocumentBuilder
implements|implements
name|DocumentBuilder
block|{
comment|/**    * A {@link TaxonomyWriter} for adding categories and retrieving their    * ordinals.    */
DECL|field|taxonomyWriter
specifier|protected
specifier|final
name|TaxonomyWriter
name|taxonomyWriter
decl_stmt|;
comment|/**    * Parameters to be used when indexing categories.    */
DECL|field|indexingParams
specifier|protected
specifier|final
name|FacetIndexingParams
name|indexingParams
decl_stmt|;
comment|/**    * A list of fields which is filled at ancestors' construction and used    * during {@link CategoryDocumentBuilder#build(Document)}.    */
DECL|field|fieldList
specifier|protected
specifier|final
name|ArrayList
argument_list|<
name|Field
argument_list|>
name|fieldList
init|=
operator|new
name|ArrayList
argument_list|<
name|Field
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|categoriesMap
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CategoryAttribute
argument_list|>
argument_list|>
name|categoriesMap
decl_stmt|;
comment|/**    * Creating a facets document builder with default facet indexing    * parameters.<br>    * See:    * {@link #CategoryDocumentBuilder(TaxonomyWriter, FacetIndexingParams)}    *     * @param taxonomyWriter    *            to which new categories will be added, as well as translating    *            known categories to ordinals    * @throws IOException    *     */
DECL|method|CategoryDocumentBuilder
specifier|public
name|CategoryDocumentBuilder
parameter_list|(
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|taxonomyWriter
argument_list|,
operator|new
name|DefaultFacetIndexingParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creating a facets document builder with a given facet indexing parameters    * object.<br>    *     * @param taxonomyWriter    *            to which new categories will be added, as well as translating    *            known categories to ordinals    * @param params    *            holds all parameters the indexing process should use such as    *            category-list parameters    * @throws IOException    */
DECL|method|CategoryDocumentBuilder
specifier|public
name|CategoryDocumentBuilder
parameter_list|(
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|,
name|FacetIndexingParams
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|taxonomyWriter
operator|=
name|taxonomyWriter
expr_stmt|;
name|this
operator|.
name|indexingParams
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|categoriesMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CategoryAttribute
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set the categories of the document builder from an {@link Iterable} of    * {@link CategoryPath} objects.    *     * @param categoryPaths    *            An iterable of CategoryPath objects which holds the categories    *            (facets) which will be added to the document at    *            {@link #build(Document)}    * @return This CategoryDocumentBuilder, to enable this one line call:    *         {@code new} {@link #CategoryDocumentBuilder(TaxonomyWriter)}.    *         {@link #setCategoryPaths(Iterable)}.{@link #build(Document)}.    * @throws IOException    */
DECL|method|setCategoryPaths
specifier|public
name|CategoryDocumentBuilder
name|setCategoryPaths
parameter_list|(
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
name|categoryPaths
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|categoryPaths
operator|==
literal|null
condition|)
block|{
name|fieldList
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
return|return
name|setCategories
argument_list|(
operator|new
name|CategoryAttributesIterable
argument_list|(
name|categoryPaths
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Set the categories of the document builder from an {@link Iterable} of    * {@link CategoryAttribute} objects.    *     * @param categories    *            An iterable of {@link CategoryAttribute} objects which holds    *            the categories (facets) which will be added to the document at    *            {@link #build(Document)}    * @return This CategoryDocumentBuilder, to enable this one line call:    *         {@code new} {@link #CategoryDocumentBuilder(TaxonomyWriter)}.    *         {@link #setCategories(Iterable)}.{@link #build(Document)}.    * @throws IOException    */
DECL|method|setCategories
specifier|public
name|CategoryDocumentBuilder
name|setCategories
parameter_list|(
name|Iterable
argument_list|<
name|CategoryAttribute
argument_list|>
name|categories
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldList
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|categories
operator|==
literal|null
condition|)
block|{
return|return
name|this
return|;
block|}
comment|// get field-name to a list of facets mapping as different facets could
comment|// be added to different category-lists on different fields
name|fillCategoriesMap
argument_list|(
name|categories
argument_list|)
expr_stmt|;
comment|// creates a different stream for each different field
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CategoryAttribute
argument_list|>
argument_list|>
name|e
range|:
name|categoriesMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// create a category attributes stream for the array of facets
name|CategoryAttributesStream
name|categoryAttributesStream
init|=
operator|new
name|CategoryAttributesStream
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
comment|// Set a suitable {@link TokenStream} using
comment|// CategoryParentsStream, followed by CategoryListTokenizer and
comment|// CategoryTokenizer composition (the ordering of the last two is
comment|// not mandatory).
name|CategoryParentsStream
name|parentsStream
init|=
operator|(
name|CategoryParentsStream
operator|)
name|getParentsStream
argument_list|(
name|categoryAttributesStream
argument_list|)
decl_stmt|;
name|CategoryListTokenizer
name|categoryListTokenizer
init|=
name|getCategoryListTokenizer
argument_list|(
name|parentsStream
argument_list|)
decl_stmt|;
name|CategoryTokenizer
name|stream
init|=
name|getCategoryTokenizer
argument_list|(
name|categoryListTokenizer
argument_list|)
decl_stmt|;
comment|// Finally creating a suitable field with stream and adding it to a
comment|// master field-list, used during the build process (see
comment|// super.build())
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fieldList
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|stream
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**    * Get a stream of categories which includes the parents, according to    * policies defined in indexing parameters.    *     * @param categoryAttributesStream    *            The input stream    * @return The parents stream.    * @see OrdinalPolicy OrdinalPolicy (for policy of adding category tokens for parents)    * @see PathPolicy PathPolicy (for policy of adding category<b>list</b> tokens for parents)    */
DECL|method|getParentsStream
specifier|protected
name|TokenStream
name|getParentsStream
parameter_list|(
name|CategoryAttributesStream
name|categoryAttributesStream
parameter_list|)
block|{
return|return
operator|new
name|CategoryParentsStream
argument_list|(
name|categoryAttributesStream
argument_list|,
name|taxonomyWriter
argument_list|,
name|indexingParams
argument_list|)
return|;
block|}
comment|/**    * Fills the categories mapping between a field name and a list of    * categories that belongs to it according to this builder's    * {@link FacetIndexingParams} object    *     * @param categories    *            Iterable over the category attributes    */
DECL|method|fillCategoriesMap
specifier|protected
name|void
name|fillCategoriesMap
parameter_list|(
name|Iterable
argument_list|<
name|CategoryAttribute
argument_list|>
name|categories
parameter_list|)
throws|throws
name|IOException
block|{
name|categoriesMap
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// for-each category
for|for
control|(
name|CategoryAttribute
name|category
range|:
name|categories
control|)
block|{
comment|// extracting the field-name to which this category belongs
name|String
name|fieldName
init|=
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|category
operator|.
name|getCategoryPath
argument_list|()
argument_list|)
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
decl_stmt|;
comment|// getting the list of categories which belongs to that field
name|List
argument_list|<
name|CategoryAttribute
argument_list|>
name|list
init|=
name|categoriesMap
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
comment|// if no such list exists
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
comment|// adding a new one to the map
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|CategoryAttribute
argument_list|>
argument_list|()
expr_stmt|;
name|categoriesMap
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
comment|// adding the new category to the list
name|list
operator|.
name|add
argument_list|(
name|category
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get a category list tokenizer (or a series of such tokenizers) to create    * the<b>category list tokens</b>.    *     * @param categoryStream    *            A stream containing {@link CategoryAttribute} with the    *            relevant data.    * @return The category list tokenizer (or series of tokenizers) to be used    *         in creating category list tokens.    */
DECL|method|getCategoryListTokenizer
specifier|protected
name|CategoryListTokenizer
name|getCategoryListTokenizer
parameter_list|(
name|TokenStream
name|categoryStream
parameter_list|)
block|{
return|return
name|getCountingListTokenizer
argument_list|(
name|categoryStream
argument_list|)
return|;
block|}
comment|/**    * Get a {@link CountingListTokenizer} for creating counting list token.    *     * @param categoryStream    *            A stream containing {@link CategoryAttribute}s with the    *            relevant data.    * @return A counting list tokenizer to be used in creating counting list    *         token.    */
DECL|method|getCountingListTokenizer
specifier|protected
name|CountingListTokenizer
name|getCountingListTokenizer
parameter_list|(
name|TokenStream
name|categoryStream
parameter_list|)
block|{
return|return
operator|new
name|CountingListTokenizer
argument_list|(
name|categoryStream
argument_list|,
name|indexingParams
argument_list|)
return|;
block|}
comment|/**    * Get a {@link CategoryTokenizer} to create the<b>category tokens</b>.    * This method can be overridden for adding more attributes to the category    * tokens.    *     * @param categoryStream    *            A stream containing {@link CategoryAttribute} with the    *            relevant data.    * @return The {@link CategoryTokenizer} to be used in creating category    *         tokens.    * @throws IOException    */
DECL|method|getCategoryTokenizer
specifier|protected
name|CategoryTokenizer
name|getCategoryTokenizer
parameter_list|(
name|TokenStream
name|categoryStream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CategoryTokenizer
argument_list|(
name|categoryStream
argument_list|,
name|indexingParams
argument_list|)
return|;
block|}
comment|/**    * Adds the fields created in one of the "set" methods to the document    */
DECL|method|build
specifier|public
name|Document
name|build
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
for|for
control|(
name|Field
name|f
range|:
name|fieldList
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
block|}
end_class

end_unit

