# 1주차 (5/10 ~ 5/14)
>## **AWS Cloud Tag**
>>### **VPC**
>>- 배포된 지역을 나타낸다.
>>- Dev / Stg / Prod 를 접미사로 사용한다.
>>    ex) Zenly-ap-ne2-Dev,  Zenly-ap-ne2-Stg, Zenly-ap-ne2-Prod
>>
>>### **Subnet**
>>
>>- VPC + Scope + Availability Zone
>>    - Scope = Public/Private
>>
>>    ex) Zenly-ap-ne2-Dev-Public-a
>>
>>### **EC2**
>>
>>- <Project>whatever your team needs-<(dev/Stg/prod/whatever)>-EC2
>>
>>    ex) Zenly-APISvr-Dev-EC2
>>
>>### **S3**
>>
>>- <Project>whatever your team needs-<(dev/Stg/prod/whatever)>-S3
>>
>>    ex) Zenly-StaticHosting-Dev-S3
>## **Terraform Convention**
>>- 각 중첩 수준에 대해 두 개의 공백을 들여 씁니다.
>>
>>    ```
>>    resource "aws_route_table_association" "fotk_public_route_a" {
>>      subnet_id      = aws_subnet.fotk_public_a.id
>>      route_table_id = aws_route_table.fotk_public_route.id
>>    }
>>    ```
>>
>>- 단일 행 값이 있는 여러 인수가 동일한 중첩 수준에서 연속 된 행에 나타나면 등호를 정렬합니다.
>>
>>    ```
>>    subnet_id      = aws_subnet.fotk_public_a.id
>>    route_table_id = aws_route_table.fotk_public_route.id
>>    ```
>>
>>- 인수와 블록이 모두 블록 본문 안에 함께 나타나면 모든 인수를 맨 위에 함께 배치 한 다음 그 아래에 >>중첩 된 블록을 배치합니다. 하나의 빈 행을 사용하여 블록에서 인수를 분리하십시오.
>>- 한 블록 내의 논리적 그룹을 나눌 때는 한 줄 떨어져 있어야 한다.
>>- 가장 상위 블록은 그 다음 블록과 반드시 한 줄 떨어져 있어야 한다.
>>- 중첩된 블록도 다른 블록과는 한 줄 떨어져 있어야 한다.
>## **느낀점**
>위에 `aws cloud tag`, `terraform convention`외에도 이슈를 어떻게 관리할지에 대한 규칙을 정하는 시간이었습니다.\
>다만 이런 경험이 없어서 이슈와 관련해서 어떻게 기록하는지에 대한 의문점이 많이 남았습니다.
# 2주차 (5/17 ~ 5/21)
>## **Jira & Confluence**
>## **느낀점**
>> 칸반보드, 스프린트등 워크플로우를 관리하는 방법에 대해 학습하면서 멘토님이 강조한 `Agile`을 어떻게 달성해나가야할지에 대해 생각할 수 있어서 유익했습니다.
>> 각자의 `hr`을 명확히 측정하는게 왜 중요한지에 대해서 많이 느낄수 있었음.
# 3주차 (5/24 ~ 5/28)
>## **AWS CodePipeline**
>>### **AWS Console**
>>![CI_pipeline_console](../../../../phase0/img/CI_Console.png)
>>### **Terraform**
>>![CI_pipeline_Terraform](../../../../phase0/img/CI_Terraform.png)
>## **느낀점**
>>스프린트 기간인 3일동안 어느 정도의 `Task`를 할수 있는지 측정해보기 위해 `CI`파이프라인 구축을 해보았습니다.\
>> `AWS Console`을 이용한 부분에 있어서는 생각보다 빠르게 끝났지만 `Terraform`을 이용해 `IaC`로 관리하는 부분에서 엄청나게 시간사용하는것을 확인했습니다.\
>> 스스로 생각했던것보다 훨씬 능력이 부족하다는 것을 많이 느낄수 있었습니다.\
>> 또한 스프린트에 걸린 `Task`를 처리하다보니 다른 팀에서 요청한 이슈를 스프린트 기간내에 처리하지 못해 전체적으로 계획이 밀리는 것을 확인하며 스프린트에 `Task`를 발행할떄 나의 역량과 우선순위를 기준으로 여유를 두고 발행해야겠다고 생각했습니다.
# 4주차 (5/31 ~ 6/4)
> ## **장애대응**
>>![Error_Report](../../../../phase0/img/Error_Report.png)
>>![Jira_Issue1](../../../../phase0/img/Jira_Issue1.png)
>>![Jira_Issue2](../../../../phase0/img/Jira_Issue2.png)
>> 3주차때부터 빈번하게 발생하던 문제였으나 원인을 파악하지 못하고 회고, 버퍼기간에 추가적 조사를 통해 대략적으로 하드웨어 스펙중 메모리 부족으로 인한 원인으로 추정하기 시작했습니다.\
>> 추정이라고 하는 이유는 `AWS CloudWatch`에서 제공해주는 모니터링 값중 메모리에 대한 부분이 없어서 명확하게 확정지을수가 없었기 때문입니다.\
>> 개별적 모니터링 툴이 필요하다는 점을 학습할수 있었습니다.\
>> 또한 장애보고 이후 해결까지 시간을 측정하면서 기록을 남겨뒀습니다.
> ## **Prometheus**
>>![Prometheus_Setup](../../../../phase0/img/Prometheus_Setup.png)
>>![Prometheus_Monitor](../../../../phase0/img/Prometheus_Monitor.png)
>> 하드웨어적 요소에서 발생한 장애중 `CloudWatch`로 모니터링 하지 못하는 부분으로 인해서 어떤것을 도입할까 고민하던중 `ft_services`에서 했던 `TIG`스택 `Telegraf`, `InfluxDB`, `Grafana`를 도입할까하다가 `Kubernets`모니터링 표준인 `Prometheus`를 테스트 삼아 도입해봤습니다.\
>> 구체적인 원리를 모른체 사용하는 것이라 의도한 매트릭은 정상적으로 수집하는것으로 확인했으나 추후에 다양한 모니터링 툴들을 공부하기로 결정했습니다.
> ## **느낀점**
>> 4주차에 `CD`파이프라인을 구축하기로 `Task`를 발행했지만 내가 알고 있는 지식수준에서는 온전히 구현할수 없는 점을 확인했습니다.\
>> 특히 스프린트 기간에는 학습을 하지 않고 내가 가진 지식수준으로 발행한 `Task`를 해결하려고 시도하는데 얼마나 지식이 부족한지 느낄수 있었습니다.
# Phase1 계획
* Prometheus 도입
* CD파이프라인 구축
* Sonarqube 도입