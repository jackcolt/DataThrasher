package etl.pharma


  case class PublicationType(
                              UI: String,
                              content: String
                            )
  case class PublicationTypeList(
                                  PublicationType: PublicationType
                                )
  case class ArticleDate(
                          Month: String,
                          Year: String,
                          DateType: String,
                          Day: String
                        )
  case class AbstractBis(
                          CopyrightInformation: String,
                          AbstractText: String
                        )
  case class AffiliationInfo(
                              Affiliation: String
                            )
  case class Author(
                     ValidYN: String,
                     ForeName: String,
                     LastName: String,
                     Initials: String,
                     AffiliationInfo: AffiliationInfo
                   )
  case class AuthorList(
                         //CompleteYN: String,
                         Author: List[Author]
                       )
  case class ELocationID(
                          ValidYN: String,
                          EIdType: String,
                          content: String
                        )
  case class PubDate(
                      Month: String,
                      Year: Double,
                      Day: String
                    )
  case class JournalIssue(
                           PubDate: PubDate,
                           CitedMedium: String
                         )
  case class ISSN(
                   IssnType: String,
                   content: String
                 )
  case class Journal(
                      //JournalIssue: JournalIssue,
                      //ISOAbbreviation: String,
                      ISSN: ISSN,
                      Title: String
                    )
  case class Article(
                      PublicationTypeList: PublicationTypeList,
                      ArticleDate: ArticleDate,
                      Language: String,
                      Abstract: AbstractBis,
                      AuthorList: AuthorList,
                      ArticleTitle: String,
                      ELocationID: ELocationID,
                      Journal: Journal,
                      PubModel: String
                    )

  case class R00tJsonObject(
                             Article: Article
                           )

