begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.gdata.server
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
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
name|io
operator|.
name|PrintWriter
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|GDataRequest
operator|.
name|OutputFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|MockControl
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|ExtensionProfile
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|Feed
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|PlainTextConstruct
import|;
end_import

begin_comment
comment|/**   *    * @author Simon Willnauer   *   */
end_comment

begin_class
DECL|class|TestGDataResponse
specifier|public
class|class
name|TestGDataResponse
extends|extends
name|TestCase
block|{
DECL|field|response
specifier|private
name|GDataResponse
name|response
decl_stmt|;
DECL|field|httpResponse
specifier|private
name|HttpServletResponse
name|httpResponse
decl_stmt|;
DECL|field|control
specifier|private
name|MockControl
name|control
decl_stmt|;
DECL|field|generatedFeedAtom
specifier|private
specifier|static
name|String
name|generatedFeedAtom
init|=
literal|"<?xml version='1.0'?><feed xmlns='http://www.w3.org/2005/Atom' xmlns:openSearch='http://a9.com/-/spec/opensearchrss/1.0/'><entry><title type='text'>Test</title></entry></feed>"
decl_stmt|;
DECL|field|generatedEntryAtom
specifier|private
specifier|static
name|String
name|generatedEntryAtom
init|=
literal|"<?xml version='1.0'?><entry xmlns='http://www.w3.org/2005/Atom'><title type='text'>Test</title></entry>"
decl_stmt|;
DECL|field|generatedFeedRSS
specifier|private
specifier|static
name|String
name|generatedFeedRSS
init|=
literal|"<?xml version='1.0'?><rss xmlns:atom='http://www.w3.org/2005/Atom' xmlns:openSearch='http://a9.com/-/spec/opensearchrss/1.0/' version='2.0'><channel><description></description><item><title>Test</title></item></channel></rss>"
decl_stmt|;
DECL|field|generatedEntryRSS
specifier|private
specifier|static
name|String
name|generatedEntryRSS
init|=
literal|"<?xml version='1.0'?><item xmlns:atom='http://www.w3.org/2005/Atom'><title>Test</title></item>"
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|control
operator|=
name|MockControl
operator|.
name|createControl
argument_list|(
name|HttpServletResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|httpResponse
operator|=
operator|(
name|HttpServletResponse
operator|)
name|this
operator|.
name|control
operator|.
name|getMock
argument_list|()
expr_stmt|;
name|this
operator|.
name|response
operator|=
operator|new
name|GDataResponse
argument_list|(
name|this
operator|.
name|httpResponse
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testConstructor
specifier|public
name|void
name|testConstructor
parameter_list|()
block|{
try|try
block|{
operator|new
name|GDataResponse
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"IllegalArgumentExceptin expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// TODO: handle exception
block|}
block|}
comment|/*       * Test method for 'org.apache.lucene.gdata.server.GDataResponse.sendResponse(BaseFeed, ExtensionProfile)'       */
DECL|method|testSendResponseBaseFeedExtensionProfile
specifier|public
name|void
name|testSendResponseBaseFeedExtensionProfile
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|Feed
name|f
init|=
literal|null
decl_stmt|;
name|this
operator|.
name|response
operator|.
name|sendResponse
argument_list|(
name|f
argument_list|,
operator|new
name|ExtensionProfile
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|//
block|}
try|try
block|{
name|Feed
name|f
init|=
name|createFeed
argument_list|()
decl_stmt|;
name|this
operator|.
name|response
operator|.
name|sendResponse
argument_list|(
name|f
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|//
block|}
name|StringWriter
name|stringWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|stringWriter
argument_list|)
decl_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|httpResponse
operator|.
name|getWriter
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|this
operator|.
name|response
operator|.
name|setOutputFormat
argument_list|(
name|OutputFormat
operator|.
name|ATOM
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|response
operator|.
name|sendResponse
argument_list|(
name|createFeed
argument_list|()
argument_list|,
operator|new
name|ExtensionProfile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Simple XML representation"
argument_list|,
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|,
name|generatedFeedAtom
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
name|stringWriter
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|PrintWriter
argument_list|(
name|stringWriter
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|httpResponse
operator|.
name|getWriter
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|this
operator|.
name|response
operator|.
name|setOutputFormat
argument_list|(
name|OutputFormat
operator|.
name|RSS
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|response
operator|.
name|sendResponse
argument_list|(
name|createFeed
argument_list|()
argument_list|,
operator|new
name|ExtensionProfile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Simple XML representation"
argument_list|,
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|,
name|generatedFeedRSS
argument_list|)
expr_stmt|;
block|}
comment|/*       * Test method for 'org.apache.lucene.gdata.server.GDataResponse.sendResponse(BaseEntry, ExtensionProfile)'       */
DECL|method|testSendResponseBaseEntryExtensionProfile
specifier|public
name|void
name|testSendResponseBaseEntryExtensionProfile
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|Entry
name|e
init|=
literal|null
decl_stmt|;
name|this
operator|.
name|response
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|,
operator|new
name|ExtensionProfile
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|//
block|}
try|try
block|{
name|Entry
name|e
init|=
name|createEntry
argument_list|()
decl_stmt|;
name|this
operator|.
name|response
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|//
block|}
comment|//        // test Atom output
name|StringWriter
name|stringWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|stringWriter
argument_list|)
decl_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|httpResponse
operator|.
name|getWriter
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|this
operator|.
name|response
operator|.
name|setOutputFormat
argument_list|(
name|OutputFormat
operator|.
name|ATOM
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|response
operator|.
name|sendResponse
argument_list|(
name|createEntry
argument_list|()
argument_list|,
operator|new
name|ExtensionProfile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Simple XML representation ATOM"
argument_list|,
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|,
name|generatedEntryAtom
argument_list|)
expr_stmt|;
comment|// test rss output
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
name|stringWriter
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|PrintWriter
argument_list|(
name|stringWriter
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|httpResponse
operator|.
name|getWriter
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|this
operator|.
name|response
operator|.
name|setOutputFormat
argument_list|(
name|OutputFormat
operator|.
name|RSS
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|response
operator|.
name|sendResponse
argument_list|(
name|createEntry
argument_list|()
argument_list|,
operator|new
name|ExtensionProfile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Simple XML representation RSS"
argument_list|,
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|,
name|generatedEntryRSS
argument_list|)
expr_stmt|;
block|}
comment|/* create a simple feed */
DECL|method|createFeed
specifier|private
name|Feed
name|createFeed
parameter_list|()
block|{
name|Feed
name|feed
init|=
operator|new
name|Feed
argument_list|()
decl_stmt|;
name|feed
operator|.
name|getEntries
argument_list|()
operator|.
name|add
argument_list|(
name|createEntry
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|feed
return|;
block|}
comment|/* create a simple entry */
DECL|method|createEntry
specifier|private
name|Entry
name|createEntry
parameter_list|()
block|{
name|Entry
name|e
init|=
operator|new
name|Entry
argument_list|()
decl_stmt|;
name|e
operator|.
name|setTitle
argument_list|(
operator|new
name|PlainTextConstruct
argument_list|(
literal|"Test"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
block|}
end_class

end_unit

