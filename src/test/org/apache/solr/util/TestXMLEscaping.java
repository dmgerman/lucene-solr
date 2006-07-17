begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

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
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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

begin_comment
comment|/** Test (some of the) character escaping functions of the XML class  *  $Id$  */
end_comment

begin_class
DECL|class|TestXMLEscaping
specifier|public
class|class
name|TestXMLEscaping
extends|extends
name|TestCase
block|{
DECL|method|doSimpleTest
specifier|private
name|void
name|doSimpleTest
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|expectedOutput
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|XML
operator|.
name|escapeCharData
argument_list|(
name|input
argument_list|,
name|sw
argument_list|)
expr_stmt|;
specifier|final
name|String
name|result
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Escaped output matches '"
operator|+
name|expectedOutput
operator|+
literal|"'"
argument_list|,
name|result
argument_list|,
name|expectedOutput
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoEscape
specifier|public
name|void
name|testNoEscape
parameter_list|()
throws|throws
name|IOException
block|{
name|doSimpleTest
argument_list|(
literal|"Bonnie"
argument_list|,
literal|"Bonnie"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAmpAscii
specifier|public
name|void
name|testAmpAscii
parameter_list|()
throws|throws
name|IOException
block|{
name|doSimpleTest
argument_list|(
literal|"Bonnie& Clyde"
argument_list|,
literal|"Bonnie&amp; Clyde"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAmpAndTagAscii
specifier|public
name|void
name|testAmpAndTagAscii
parameter_list|()
throws|throws
name|IOException
block|{
name|doSimpleTest
argument_list|(
literal|"Bonnie& Cl<em>y</em>de"
argument_list|,
literal|"Bonnie&amp; Cl&lt;em>y&lt;/em>de"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAmpWithAccents
specifier|public
name|void
name|testAmpWithAccents
parameter_list|()
throws|throws
name|IOException
block|{
comment|// 00e9 is unicode eacute
name|doSimpleTest
argument_list|(
literal|"Les \u00e9v\u00e9nements chez Bonnie& Clyde"
argument_list|,
literal|"Les \u00e9v\u00e9nements chez Bonnie&amp; Clyde"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAmpDotWithAccents
specifier|public
name|void
name|testAmpDotWithAccents
parameter_list|()
throws|throws
name|IOException
block|{
comment|// 00e9 is unicode eacute
name|doSimpleTest
argument_list|(
literal|"Les \u00e9v\u00e9nements chez Bonnie& Clyde."
argument_list|,
literal|"Les \u00e9v\u00e9nements chez Bonnie&amp; Clyde."
argument_list|)
expr_stmt|;
block|}
DECL|method|testAmpAndTagWithAccents
specifier|public
name|void
name|testAmpAndTagWithAccents
parameter_list|()
throws|throws
name|IOException
block|{
comment|// 00e9 is unicode eacute
name|doSimpleTest
argument_list|(
literal|"Les \u00e9v\u00e9nements<chez/> Bonnie& Clyde"
argument_list|,
literal|"Les \u00e9v\u00e9nements&lt;chez/> Bonnie&amp; Clyde"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

