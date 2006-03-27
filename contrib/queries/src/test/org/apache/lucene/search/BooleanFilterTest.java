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
name|BitSet
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
name|WhitespaceAnalyzer
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
name|index
operator|.
name|IndexReader
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
name|IndexWriter
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
name|search
operator|.
name|RangeFilter
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
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|BooleanFilterTest
specifier|public
class|class
name|BooleanFilterTest
extends|extends
name|TestCase
block|{
DECL|field|directory
specifier|private
name|RAMDirectory
name|directory
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//Add series of docs with filterable fields : acces rights, prices, dates and "in-stock" flags
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"admin guest"
argument_list|,
literal|"010"
argument_list|,
literal|"20040101"
argument_list|,
literal|"Y"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"guest"
argument_list|,
literal|"020"
argument_list|,
literal|"20040101"
argument_list|,
literal|"Y"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"guest"
argument_list|,
literal|"020"
argument_list|,
literal|"20050101"
argument_list|,
literal|"Y"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"admin"
argument_list|,
literal|"020"
argument_list|,
literal|"20050101"
argument_list|,
literal|"Maybe"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"admin guest"
argument_list|,
literal|"030"
argument_list|,
literal|"20050101"
argument_list|,
literal|"N"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|accessRights
parameter_list|,
name|String
name|price
parameter_list|,
name|String
name|date
parameter_list|,
name|String
name|inStock
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"accessRights"
argument_list|,
name|accessRights
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"price"
argument_list|,
name|price
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"date"
argument_list|,
name|date
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"inStock"
argument_list|,
name|inStock
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|getRangeFilter
specifier|private
name|Filter
name|getRangeFilter
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|lowerPrice
parameter_list|,
name|String
name|upperPrice
parameter_list|)
block|{
return|return
operator|new
name|RangeFilter
argument_list|(
name|field
argument_list|,
name|lowerPrice
argument_list|,
name|upperPrice
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getTermsFilter
specifier|private
name|TermsFilter
name|getTermsFilter
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|TermsFilter
name|tf
init|=
operator|new
name|TermsFilter
argument_list|()
decl_stmt|;
name|tf
operator|.
name|addTerm
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|tf
return|;
block|}
DECL|method|testShould
specifier|public
name|void
name|testShould
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"price"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|BitSet
name|bits
init|=
name|booleanFilter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should retrieves only 1 doc"
argument_list|,
literal|1
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testShoulds
specifier|public
name|void
name|testShoulds
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"010"
argument_list|,
literal|"020"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"020"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|BitSet
name|bits
init|=
name|booleanFilter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Shoulds are Ored together"
argument_list|,
literal|5
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testShouldsAndMustNot
specifier|public
name|void
name|testShouldsAndMustNot
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"010"
argument_list|,
literal|"020"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"020"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"inStock"
argument_list|,
literal|"N"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
name|BitSet
name|bits
init|=
name|booleanFilter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Shoulds Ored but AndNot"
argument_list|,
literal|4
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"inStock"
argument_list|,
literal|"Maybe"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|=
name|booleanFilter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Shoulds Ored but AndNots"
argument_list|,
literal|3
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testShouldsAndMust
specifier|public
name|void
name|testShouldsAndMust
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"010"
argument_list|,
literal|"020"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"020"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"accessRights"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|BitSet
name|bits
init|=
name|booleanFilter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Shoulds Ored but MUST"
argument_list|,
literal|3
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testShouldsAndMusts
specifier|public
name|void
name|testShouldsAndMusts
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"010"
argument_list|,
literal|"020"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"020"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"accessRights"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"date"
argument_list|,
literal|"20040101"
argument_list|,
literal|"20041231"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|BitSet
name|bits
init|=
name|booleanFilter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Shoulds Ored but MUSTs ANDED"
argument_list|,
literal|1
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testShouldsAndMustsAndMustNot
specifier|public
name|void
name|testShouldsAndMustsAndMustNot
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"price"
argument_list|,
literal|"030"
argument_list|,
literal|"040"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"accessRights"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getRangeFilter
argument_list|(
literal|"date"
argument_list|,
literal|"20050101"
argument_list|,
literal|"20051231"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"inStock"
argument_list|,
literal|"N"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
name|BitSet
name|bits
init|=
name|booleanFilter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Shoulds Ored but MUSTs ANDED and MustNot"
argument_list|,
literal|0
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testJustMust
specifier|public
name|void
name|testJustMust
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"accessRights"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|BitSet
name|bits
init|=
name|booleanFilter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"MUST"
argument_list|,
literal|3
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testJustMustNot
specifier|public
name|void
name|testJustMustNot
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"inStock"
argument_list|,
literal|"N"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
name|BitSet
name|bits
init|=
name|booleanFilter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"MUST_NOT"
argument_list|,
literal|4
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMustAndMustNot
specifier|public
name|void
name|testMustAndMustNot
parameter_list|()
throws|throws
name|Throwable
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"inStock"
argument_list|,
literal|"N"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|getTermsFilter
argument_list|(
literal|"price"
argument_list|,
literal|"030"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
name|BitSet
name|bits
init|=
name|booleanFilter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"MUST_NOT wins over MUST for same docs"
argument_list|,
literal|0
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

