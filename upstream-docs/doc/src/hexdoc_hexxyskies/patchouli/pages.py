from hexdoc.patchouli.page import PageWithTitle


class ExamplePage(PageWithTitle, type="hexxyskies:example"):
    """This is the Pydantic model for the `hexxyskies:example` page type."""

    example_value: str
